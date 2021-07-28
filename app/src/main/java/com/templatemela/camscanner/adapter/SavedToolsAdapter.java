package com.templatemela.camscanner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.camscanner.R;
import com.templatemela.camscanner.models.SavedToolModel;
import com.templatemela.camscanner.models.SavedToolType;

import java.util.ArrayList;

public class SavedToolsAdapter extends RecyclerView.Adapter<SavedToolsAdapter.ViewHolder> {

    public OnSavedToolSelected onSavedToolSelected;

    public ArrayList<SavedToolModel> savedToolsList = new ArrayList<>();

    public interface OnSavedToolSelected {
        void onSavedToolSelected(SavedToolType savedToolType);
    }

    public SavedToolsAdapter(OnSavedToolSelected onSavedToolSelected2) {
        onSavedToolSelected = onSavedToolSelected2;
        savedToolsList.add(new SavedToolModel(R.drawable.edit_save, SavedToolType.EDIT, "Edit"));
        savedToolsList.add(new SavedToolModel(R.drawable.pdf_save, SavedToolType.OPENPDF, "Open pdf"));
        savedToolsList.add(new SavedToolModel(R.drawable.rename_white, SavedToolType.NAME, "Rename"));
        savedToolsList.add(new SavedToolModel(R.drawable.rotate_save, SavedToolType.ROTATE, "Rotate"));
        savedToolsList.add(new SavedToolModel(R.drawable.note_save, SavedToolType.NOTE, "Note"));
        savedToolsList.add(new SavedToolModel(R.drawable.img_to_text_save, SavedToolType.ImageToText, "Image to text"));
        savedToolsList.add(new SavedToolModel(R.drawable.share_save, SavedToolType.SHARE, "Share"));
        savedToolsList.add(new SavedToolModel(R.drawable.delete_save, SavedToolType.DELETE, "Delete"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_tools_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.iv_toolIcon.setImageResource(savedToolsList.get(i).getSaved_tool_icon());
        viewHolder.txtIconName.setText(savedToolsList.get(i).getIcon_name());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSavedToolSelected.onSavedToolSelected(((SavedToolModel) savedToolsList.get(i)).getSavedToolType());
            }
        });
    }

    @Override
    public int getItemCount() {
        return savedToolsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_toolIcon;
        TextView txtIconName;

        public ViewHolder(View view) {
            super(view);
            iv_toolIcon = (ImageView) view.findViewById(R.id.iv_toolIcon);
            txtIconName = (TextView) view.findViewById(R.id.txtIconName);
        }
    }
}
