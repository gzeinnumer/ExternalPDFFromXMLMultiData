package com.gzeinnumer.externalpdffromxmlmultidata.helper.functionGlobalPDFMulti;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.gzeinnumer.externalpdffromxmlmultidata.helper.FunctionGlobalDir;

import java.io.File;
import java.util.List;

public class AppUtils {

    public static Bitmap findViewBitmap(final List<RVAdapterForPDF.MyModelPDF> currentPDFModels, int deviceWidth, int deviceHeight, RVAdapterForPDF pdfRootAdapter, RecyclerView mPDFCreationRV, View mPDFCreationView) {
        pdfRootAdapter.setList(currentPDFModels);
        mPDFCreationRV.setAdapter(pdfRootAdapter);
        return getViewBitmap(mPDFCreationView, deviceWidth, deviceHeight);
    }

    private static Bitmap getViewBitmap(View view, int deviceWidth, int deviceHeight) {
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(deviceHeight, View.MeasureSpec.EXACTLY);
        view.measure(measuredWidth, measuredHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(deviceWidth, deviceHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.draw(c);
        return getResizedBitmap(b, (measuredWidth * 80) / 100, (measuredHeight * 80) / 100);
    }

    private static Bitmap getResizedBitmap(Bitmap image, int width, int height) {

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            height = (int) (width / bitmapRatio);
        } else {
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static String createPDFPath() {
        File folder = new File(FunctionGlobalDir.getStorageCard + FunctionGlobalDir.appFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder + File.separator + "pdf_.pdf";
    }

}
