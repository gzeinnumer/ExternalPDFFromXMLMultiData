package com.gzeinnumer.externalpdffromxmlmultidata.pdfMulti;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import java.util.List;

public class FunctionGlobalPDFMulti {

    private static boolean IS_MANY_PDF_FILE;
    private static final int SECTOR = 100; // Default value for one pdf file.
    private static int START;
    private static int END = SECTOR;
    private static int NO_OF_PDF_FILE = 1;
    private static int NO_OF_FILE;
    private static int LIST_SIZE;
    private static ProgressDialog progressDialog;

    private static PDFCallBack pdfCallBack;

    public interface PDFCallBack{
        void callBackPath(String path);
    }

    private static final String TAG = "FunctionGlobalPDFMulti_";

    public static void generatePdfReport(List<RVAdapterForPDF.MyModelPDF> list, Context applicationContext, Activity activity) {
        pdfCallBack = (PDFCallBack) activity;
        LIST_SIZE = list.size();
        NO_OF_FILE = LIST_SIZE / SECTOR;
        if (LIST_SIZE % SECTOR != 0) {
            NO_OF_FILE++;
        }
        if (LIST_SIZE > SECTOR) {
            IS_MANY_PDF_FILE = true;
        } else {
            END = LIST_SIZE;
        }
        createPDFFile(list, applicationContext, activity);
    }

    public static void createPDFFile(final List<RVAdapterForPDF.MyModelPDF> list, final Context context, final Activity activity) {
        final List<RVAdapterForPDF.MyModelPDF> pdfDataList = list.subList(START, END);
        PdfBitmapCache.clearMemory();
        PdfBitmapCache.initBitmapCache(context);
        final PDFCreationUtils pdfCreationUtils = new PDFCreationUtils(activity, pdfDataList, LIST_SIZE, NO_OF_PDF_FILE);
        if (NO_OF_PDF_FILE == 1) {
            createProgressBarForPDFCreation(PDFCreationUtils.TOTAL_PROGRESS_BAR, activity);
        }
        pdfCreationUtils.createPDF(new PDFCreationUtils.PDFCallback() {

            @Override
            public void onProgress(final int i) {
                setProgressNumber(i);
            }

            @Override
            public void onCreateEveryPdfFile() {
                if (IS_MANY_PDF_FILE) {
                    NO_OF_PDF_FILE++;
                    if (NO_OF_FILE == NO_OF_PDF_FILE - 1) {
                        progressDialog.dismiss();
                        createProgressBarForMergePDF(activity);
                        pdfCreationUtils.downloadAndCombinePDFs();
                    } else {
                        START = END;
                        if (LIST_SIZE % SECTOR != 0) {
                            if (NO_OF_FILE == NO_OF_PDF_FILE) {
                                END = (START - SECTOR) + LIST_SIZE % SECTOR;
                            }
                        }
                        END = SECTOR + END;
                        createPDFFile(list, context, activity);
                    }
                } else {
                    progressDialog.dismiss();
                    createProgressBarForMergePDF(activity);
                    pdfCreationUtils.downloadAndCombinePDFs();
                }
            }

            @Override
            public void onComplete(final String filePath) {
                progressDialog.dismiss();
                pdfCallBack.callBackPath(filePath);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error  " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void createProgressBarForPDFCreation(int totalProgressBar, Activity activity) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("XML layout is in created PDF file. Please wait few moment.\\nTotal PDF page :"+totalProgressBar);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(totalProgressBar);
        progressDialog.show();
    }

    public static void createProgressBarForMergePDF(Activity activity) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("All pdf file merge into one file. Please wait few moment. After done of mergig pdf file, go to pdf file path.");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void setProgressNumber(int progressNumber) {
        progressDialog.setProgress(progressNumber);
    }
}
