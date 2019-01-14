package pl.edu.agh.recorder.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultipartAWSFileSender {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int DEFAULT_BUFFER_SIZE = 20480; // ..bytes = 20KB.
    private static final long DEFAULT_EXPIRE_TIME = 604800000L; // ..ms = 1 week.
    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

    private String contentType;
    private String fileName;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Long length;
    private String filePath;
    private String bucketName;
    private AmazonS3 s3Client;

    public MultipartAWSFileSender(String filePath, AmazonS3 s3Client, String bucketName, String fileName) {
        ObjectMetadata objectMetadata = s3Client.getObjectMetadata(bucketName, filePath);

        this.fileName = fileName;
        this.filePath = filePath;
        this.bucketName = bucketName;
        this.s3Client = s3Client;
        this.length = objectMetadata.getContentLength();
        this.contentType = objectMetadata.getContentType();
    }

    public MultipartAWSFileSender with(HttpServletRequest httpRequest) {
        request = httpRequest;
        return this;
    }

    public MultipartAWSFileSender with(HttpServletResponse httpResponse) {
        response = httpResponse;
        return this;
    }

    public void serveResource() throws IOException {
        if (response == null || request == null || fileName == null) {
            return;
        }

        String ifNoneMatch = request.getHeader("If-None-Match");
        if (ifNoneMatch != null && HttpUtils.matches(ifNoneMatch, fileName)) {
            response.setHeader("ETag", fileName); // Required in 304.
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        String ifMatch = request.getHeader("If-Match");
        if (ifMatch != null && !HttpUtils.matches(ifMatch, fileName)) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        Range full = new Range(0, length - 1, length);
        List<Range> ranges = new ArrayList<>();

        String range = request.getHeader("Range");
        if (range != null) {

            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }

            String ifRange = request.getHeader("If-Range");
            if (ifRange != null && !ifRange.equals(fileName)) {
                try {
                    long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
                    if (ifRangeTime != -1) {
                        ranges.add(full);
                    }
                } catch (IllegalArgumentException ignore) {
                    ranges.add(full);
                }
            }

            if (ranges.isEmpty()) {
                for (String part : range.substring(6).split(",")) {
                    long start = Range.sublong(part, 0, part.indexOf("-"));
                    long end = Range.sublong(part, part.indexOf("-") + 1, part.length());

                    if (start == -1) {
                        start = length - end;
                        end = length - 1;
                    } else if (end == -1 || end > length - 1) {
                        end = length - 1;
                    }

                    if (start > end) {
                        response.setHeader("Content-Range", "bytes */" + length);
                        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        return;
                    }

                    ranges.add(new Range(start, end, length));
                }
            }
        }

        String disposition = "inline";

        if (contentType == null) {
            contentType = "application/octet-stream";
        } else if (!contentType.startsWith("image")) {
            String accept = request.getHeader("Accept");
            disposition = accept != null && HttpUtils.accepts(accept, contentType) ? "inline" : "attachment";
        }
        logger.debug("Content-Type : {}", contentType);
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
        logger.debug("Content-Disposition : {}", disposition);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("ETag", fileName);
        response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);




        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        OutputStream output = response.getOutputStream();
        int read;

        if (ranges.isEmpty() || ranges.get(0) == full) {

            logger.info("Return full file");
            response.setContentType(contentType);
            response.setHeader("Content-Range", "bytes " + full.start + "-" + full.end + "/" + full.total);
            response.setHeader("Content-Length", String.valueOf(full.length));

            S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, filePath));
            while ((read = object.getObjectContent().read(buffer)) > 0) {
                output.write(buffer, 0, read);
                output.flush();
            }

        } else if (ranges.size() == 1) {

            Range r = ranges.get(0);
            logger.info("Return 1 part of file : from ({}) to ({})", r.start, r.end);
            response.setContentType(contentType);
            response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
            response.setHeader("Content-Length", String.valueOf(r.length));
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, filePath);
            getObjectRequest.setRange(r.start, r.length);
            S3Object object = s3Client.getObject(getObjectRequest);
            while ((read = object.getObjectContent().read(buffer)) > 0) {
                output.write(buffer, 0, read);
                output.flush();
            }

        } else {

            response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

            ServletOutputStream sos = (ServletOutputStream) output;

            for (Range r : ranges) {
                logger.info("Return multi part of file : from ({}) to ({})", r.start, r.end);
                sos.println();
                sos.println("--" + MULTIPART_BOUNDARY);
                sos.println("Content-Type: " + contentType);
                sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

                GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, filePath);
                getObjectRequest.setRange(r.start, r.length);
                S3Object object = s3Client.getObject(getObjectRequest);
                while ((read = object.getObjectContent().read(buffer)) > 0) {
                    output.write(buffer, 0, read);
                    output.flush();
                }
            }

            sos.println();
            sos.println("--" + MULTIPART_BOUNDARY + "--");
        }
    }

    private static class Range {
        long start;
        long end;
        long length;
        long total;

        /**
         * Construct a byte range.
         * @param start Start of the byte range.
         * @param end End of the byte range.
         * @param total Total length of the byte source.
         */
        public Range(long start, long end, long total) {
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
            this.total = total;
        }

        public static long sublong(String value, int beginIndex, int endIndex) {
            String substring = value.substring(beginIndex, endIndex);
            return (substring.length() > 0) ? Long.parseLong(substring) : -1;
        }
    }
    private static class HttpUtils {
        public static boolean accepts(String acceptHeader, String toAccept) {
            String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
            Arrays.sort(acceptValues);

            return Arrays.binarySearch(acceptValues, toAccept) > -1
                    || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
                    || Arrays.binarySearch(acceptValues, "*/*") > -1;
        }

        public static boolean matches(String matchHeader, String toMatch) {
            String[] matchValues = matchHeader.split("\\s*,\\s*");
            Arrays.sort(matchValues);
            return Arrays.binarySearch(matchValues, toMatch) > -1
                    || Arrays.binarySearch(matchValues, "*") > -1;
        }
    }
}