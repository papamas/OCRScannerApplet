package com.proyekOCR.applet;

import javax.swing.JOptionPane;

/**
 * Error code to determine more exactly by administrator the error cause
 * without needed to go to log file making general error codification for
 * application
 */
public class ErrorCode {
	
	// Origin code error is XXX digits
	public static final String ORIGIN_OKMFolderService 				= "001";
	public static final String ORIGIN_OKMDocumentService 			= "002";
	public static final String ORIGIN_OKMRemoteService				= "003";
	public static final String ORIGIN_OKMDownloadService			= "004";
	public static final String ORIGIN_OKMUploadService				= "005";
	public static final String ORIGIN_OKMHttpServlet				= "006";
	public static final String ORIGIN_OKMAuthServlet				= "007";
	public static final String ORIGIN_OKMSearchService				= "008";
	public static final String ORIGIN_OKMPropertyGroupService		= "009";
	public static final String ORIGIN_OKMNotifyService 				= "010";
	public static final String ORIGIN_OKMBookmarkService 			= "011";
	public static final String ORIGIN_OKMRepositoryServlet			= "012";
	public static final String ORIGIN_OKMGeneralUtilsServletAdmin	= "013";
	public static final String ORIGIN_OKMAuthServletAdmin			= "014";
	public static final String ORIGIN_OKMSearchServiceAdmin			= "015";
	public static final String ORIGIN_OKMRemoteServiceAdmin			= "016";
	public static final String ORIGIN_OKMPropertyGroupServiceAdmin	= "017";
	public static final String ORIGIN_OKMRepositoryServletAdmin		= "018";
	public static final String ORIGIN_OKMFolderServiceAdmin			= "019";
	public static final String ORIGIN_OKMDashboardService 			= "020";
	public static final String ORIGIN_OKMWorkspaceService 			= "021";
	public static final String ORIGIN_OKMWorkflowService 			= "022";
	public static final String ORIGIN_OKMWorkflowUploadServiceAdmin	= "023";
	public static final String ORIGIN_OKMMailService	 			= "024";
	public static final String ORIGIN_OKMPropertyService	 		= "025";
	public static final String ORIGIN_OKMBrowser 					= "026";
	
	// Cause code error is XXX digits
	public static final String CAUSE_Repository 				= "001";
	public static final String CAUSE_ItemNotFound 				= "002";
	public static final String CAUSE_ItemExists 				= "003";
	public static final String CAUSE_Lock						= "004";
	public static final String CAUSE_UnLock						= "005";
	public static final String CAUSE_General 					= "006";
	public static final String CAUSE_OKMGeneral					= "007";
	public static final String CAUSE_GWTShellEnviroment			= "008";
	public static final String CAUSE_AccessDenied				= "009";
	public static final String CAUSE_UnsupportedMimeType		= "010";
	public static final String CAUSE_FileSizeExceeded			= "011";
	public static final String CAUSE_NoSuchGroup				= "012";
	public static final String CAUSE_IOException				= "013";
	public static final String CAUSE_NoSuchProperty				= "014";
	public static final String CAUSE_PathNotFound				= "015";
	public static final String CAUSE_Version					= "016";
	public static final String CAUSE_SessionLost				= "017";
	public static final String CAUSE_FileNotFoundException		= "018";
	public static final String CAUSE_ParseException				= "019";
	public static final String CAUSE_InvalidNodeTypeDefException= "020";
	public static final String CAUSE_SQLException 				= "021";
	public static final String CAUSE_Configuration 				= "022";

	/**
	 * Gets the error
	 * 
	 * The final error code returned by application is ORIGIN_CODE_ERROR + CAUSE_CODE_ERROR
	 * example ERROR 001001 = Error causes on OKMFolderService and originated by Repository Exception
	 * 
	 * @param origin The error origin
	 * @param cause The error cause
	 * @return The error
	 */
	public static String get(String origin, String cause) {
		return "OKM-" + origin + cause;
	}

	/**
	 * Display error code
	 */
	public static void displayError(String response, String path) {
		if (response.equals(get(ORIGIN_OKMUploadService, CAUSE_AccessDenied))) {
			JOptionPane.showMessageDialog(null, path, "Access denied", JOptionPane.ERROR_MESSAGE);
		} else if (response.equals(get(ORIGIN_OKMUploadService, CAUSE_PathNotFound))) {
			JOptionPane.showMessageDialog(null, path, "Path not found", JOptionPane.ERROR_MESSAGE);
		} else  if (response.equals(get(ORIGIN_OKMUploadService, CAUSE_ItemExists))) {
			JOptionPane.showMessageDialog(null, path, "Item exists", JOptionPane.ERROR_MESSAGE);
		} else if (response.equals(get(ORIGIN_OKMUploadService, CAUSE_UnsupportedMimeType))) {
			JOptionPane.showMessageDialog(null, path, "Unsupported mime type", JOptionPane.ERROR_MESSAGE);
		} else if (response.equals(get(ORIGIN_OKMUploadService, CAUSE_FileSizeExceeded))) {
			JOptionPane.showMessageDialog(null, path, "File size exceeded", JOptionPane.ERROR_MESSAGE);
		} else if (response.equals(get(ORIGIN_OKMUploadService, CAUSE_Repository))) {
			JOptionPane.showMessageDialog(null, path, "Repository error", JOptionPane.ERROR_MESSAGE);
		} else if (response.equals(get(ORIGIN_OKMUploadService, CAUSE_IOException))) {
			JOptionPane.showMessageDialog(null, path, "IO error", JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, path, "Unknown error", JOptionPane.ERROR_MESSAGE);
		}
	}
}

