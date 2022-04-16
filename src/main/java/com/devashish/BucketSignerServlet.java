package com.devashish;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.devashish.signutility.BucketSigner;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * Servlet implementation class BucketSignerServlet
 */
public class BucketSignerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static S3Presigner presigner;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Not supported: ").append("GET method not supported.");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String data = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		String presignedUrl = BucketSigner.signBucket(request.getHeader("bucketName"), request.getHeader("keyName"),data);
		response.getWriter().append("SignedUrl: ").append(presignedUrl);
	}

}
