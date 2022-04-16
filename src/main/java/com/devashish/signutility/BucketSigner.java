package com.devashish.signutility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

public class BucketSigner {
	
	private static S3Presigner presigner = null;
	
    /**
     * This methods signs a particular bucket
     * @param bucketName
     * @param keyName
     * @return
     */
    public static String signBucket(String bucketName, String keyName, String data) {
    	if(presigner==null) {
    		presigner = S3Presigner.create();
    	}
        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .contentType("text/plain")
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);


            String myURL = presignedRequest.url().toString();
            System.out.println("Presigned URL to upload a file to: " +myURL);
            System.out.println("Which HTTP method needs to be used when uploading a file: " +
                    presignedRequest.httpRequest().method());

            // Upload content to the Amazon S3 bucket by using this URL
            URL url = presignedRequest.url();

            // Create the connection and use it to upload the new object by using the presigned URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","text/plain");
            connection.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(data);
            out.close();

            connection.getResponseCode();
            int respCode = connection.getResponseCode();
            System.out.println("HTTP response code is " + respCode);
            String resp = "";
            if(respCode==200) {
            	resp = readStream(connection.getInputStream());
            } else {
            	resp = readStream(connection.getErrorStream());
            }
            System.out.println("Response: "+resp);
            return myURL;
        } catch (S3Exception e) {
            e.getStackTrace();
        } catch (IOException e) {
            e.getStackTrace();
        }
        return null;
    }
    
    private static String readStream(InputStream is) throws IOException {
    	String data = "";
    	do {
    		data+=(char)is.read();
    	} while(is.available()>0);
    	return data;
    }
    
    public static void getRidOfPresigner() {
    	presigner.close();
    }
}
