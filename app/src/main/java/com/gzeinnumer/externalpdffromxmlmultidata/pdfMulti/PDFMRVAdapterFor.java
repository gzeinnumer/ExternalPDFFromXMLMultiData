package com.gzeinnumer.externalpdffromxmlmultidata.pdfMulti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gzeinnumer.externalpdffromxmlmultidata.R;

import java.util.ArrayList;
import java.util.List;

public class PDFMRVAdapterFor extends RecyclerView.Adapter<PDFMRVAdapterFor.MyHolder> {
    private List<MyModelPDF> list = new ArrayList<>();

    public PDFMRVAdapterFor() {
    }

    public void setList(List<MyModelPDF> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
        }

        public void bindData(MyModelPDF data) {
            tv.setText(data.getName());
        }
    }

    public static class MyModelPDF {
        private final int id;
        private final String name;
        private final String code;
        private final String image;

        public MyModelPDF(int id, String name, String code, String image) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.image = image;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        public String getImage() {
            return image;
        }
    }
}
