package com.gzeinnumer.externalpdffromxmlmultidata.pdfMulti;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gzeinnumer.externalpdffromxmlmultidata.R;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PDFCreationUtils {
    private static final String TAG = "PDFCreationUtils_";

    private int deviceHeight;
    private int deviceWidth;
    private Bitmap bitmapOfView;
    private PdfDocument document;
    private int pdfModelListSize;
    private int SECTOR = 8; // default value
    private int NUMBER_OF_PAGE;
    private List<RVAdapterForPDF.MyModelPDF> mCurrentPDFModels;
    private RVAdapterForPDF pdfRootAdapter;
    private View mPDFCreationView;
    private RecyclerView mPDFCreationRV;
    public static int TOTAL_PROGRESS_BAR;
    private int mCurrentPDFIndex;
    public static List<String> filePath = new ArrayList<>();
    private Activity activity;
    private String finalPdfFile;
    private String pathForEveryPdfFile;

    private PDFCreationUtils() {
    }

    public PDFCreationUtils(Activity activity, List<RVAdapterForPDF.MyModelPDF> currentPdfModels, int totalPDFModelSize, int currentPDFIndex) {
        this.activity = activity;
        this.mCurrentPDFModels = currentPdfModels;
        this.mCurrentPDFIndex = currentPDFIndex;
        getWH();
        createForEveryPDFFilePath();
        int sizeInPixel = activity.getResources().getDimensionPixelSize(R.dimen.dp_90) + activity.getResources().getDimensionPixelSize(R.dimen.dp_30);

        mPDFCreationView = LayoutInflater.from(activity).inflate(R.layout.pdf_creation_view, null, false);

        SECTOR = deviceHeight / sizeInPixel;
        TOTAL_PROGRESS_BAR = totalPDFModelSize / SECTOR;

        mPDFCreationRV = mPDFCreationView.findViewById(R.id.recycler_view_for_pdf);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mPDFCreationRV.setLayoutManager(mLayoutManager);

        pdfRootAdapter = new RVAdapterForPDF();

        document = new PdfDocument();
        pdfModelListSize = currentPdfModels.size();

    }

    private PDFCallback callback;

    public void createPDF(PDFCallback callback) {
        this.callback = callback;

        if (pdfModelListSize <= SECTOR) {
            NUMBER_OF_PAGE = 1;

            bitmapOfView = PDFAppUtils.findViewBitmap(mCurrentPDFModels, deviceWidth, deviceHeight, pdfRootAdapter, mPDFCreationRV, mPDFCreationView);
            PDFBitmapCache.addBitmapToMemoryCache(NUMBER_OF_PAGE, bitmapOfView);
            createPdf();
        } else {
            NUMBER_OF_PAGE = pdfModelListSize / SECTOR;
            if (pdfModelListSize % SECTOR != 0) {
                NUMBER_OF_PAGE++;
            }
            Map<Integer, List<RVAdapterForPDF.MyModelPDF>> listMap = createFinalData();
            for (int PAGE_INDEX = 1; PAGE_INDEX <= NUMBER_OF_PAGE; PAGE_INDEX++) {
                List<RVAdapterForPDF.MyModelPDF> list = listMap.get(PAGE_INDEX);
                bitmapOfView = PDFAppUtils.findViewBitmap(list, deviceWidth, deviceHeight, pdfRootAdapter, mPDFCreationRV, mPDFCreationView);
                PDFBitmapCache.addBitmapToMemoryCache(PAGE_INDEX, bitmapOfView);
            }
            createPdf();
        }
    }

    private Map<Integer, List<RVAdapterForPDF.MyModelPDF>> createFinalData() {
        int START = 0;
        int END = SECTOR;
        Map<Integer, List<RVAdapterForPDF.MyModelPDF>> map = new LinkedHashMap<>();
        int INDEX = 1;
        for (int i = 0; i < NUMBER_OF_PAGE; i++) {
            if (pdfModelListSize % SECTOR != 0) {
                if (i == NUMBER_OF_PAGE - 1) {
                    END = START + pdfModelListSize % SECTOR;
                }
            }
            List<RVAdapterForPDF.MyModelPDF> list = mCurrentPDFModels.subList(START, END);
            START = END;
            END = SECTOR + END;
            map.put(INDEX, list);
            INDEX++;
        }
        return map;
    }

    private void createPdf() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int PAGE_INDEX = 1; PAGE_INDEX <= NUMBER_OF_PAGE; PAGE_INDEX++) {

                    final Bitmap b = PDFBitmapCache.getBitmapFromMemCache(PAGE_INDEX);
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(b.getWidth(), b.getHeight(), PAGE_INDEX).create();
                    PdfDocument.Page page = document.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.parseColor("#ffffff"));
                    canvas.drawPaint(paint);
                    canvas.drawBitmap(b, 0, 0, null);
                    document.finishPage(page);

                    File filePath = new File(pathForEveryPdfFile);
                    try {
                        document.writeTo(new FileOutputStream(filePath));
                    } catch (IOException e) {
                        if (callback != null) {
                            if (e != null) {
                                callback.onError(e);
                            } else {
                                callback.onError(new Exception("IOException"));
                            }
                        }
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onProgress(progressCount++);
                        }
                    });

                    if (PAGE_INDEX == NUMBER_OF_PAGE) {
                        document.close();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onCreateEveryPdfFile();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public static int progressCount = 1;

    public void downloadAndCombinePDFs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mCurrentPDFIndex == 1) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onComplete(pathForEveryPdfFile);
                            }
                        }
                    });
                } else {
                    try {
                        PDFMergerUtility ut = new PDFMergerUtility();
                        for (String s : filePath) {
                            ut.addSource(s);
                        }
                        final FileOutputStream fileOutputStream = new FileOutputStream(new File(createFinalPdfFilePath()));
                        try {
                            ut.setDestinationStream(fileOutputStream);
                            ut.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());

                        } finally {
                            fileOutputStream.close();
                        }
                    } catch (Exception e) {

                    }
                    // delete of other pdf file
                    for (String s : filePath) {
                        new File(s).delete();
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onComplete(finalPdfFile);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void createForEveryPDFFilePath() {
        pathForEveryPdfFile = PDFAppUtils.createPDFPath();
        filePath.add(pathForEveryPdfFile);
    }

    private String createFinalPdfFilePath() {
        finalPdfFile = PDFAppUtils.createPDFPath();
        return finalPdfFile;
    }

    private void getWH() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        DirPDF.myLogD(TAG, "getWH: " + deviceHeight + "_" + deviceWidth);
    }

    public interface PDFCallback {
        void onProgress(int progress);
        void onCreateEveryPdfFile();
        void onComplete(String filePath);
        void onError(Exception e);
    }
}
