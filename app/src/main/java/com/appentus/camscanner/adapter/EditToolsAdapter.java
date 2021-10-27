package com.appentus.camscanner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appentus.camscanner.R;
import com.appentus.camscanner.models.EditToolModel;
import com.appentus.camscanner.models.EditToolType;

import java.util.ArrayList;

public class EditToolsAdapter extends RecyclerView.Adapter<EditToolsAdapter.ViewHolder> {

    public OnToolSelected onToolSelected;

    public ArrayList<EditToolModel> toolsList = new ArrayList<>();

    public interface OnToolSelected {
        void onToolSelected(EditToolType editToolType);
    }

    public EditToolsAdapter(OnToolSelected onToolSelected2) {
        onToolSelected = onToolSelected2;
        toolsList.add(new EditToolModel(R.drawable.color_filter, EditToolType.COLORFILTER,"Color Filter"));
        toolsList.add(new EditToolModel(R.drawable.adjust, EditToolType.ADJUST,"Adjust"));
        toolsList.add(new EditToolModel(R.drawable.highlight, EditToolType.HIGHLIGHT,"Highlight"));
        toolsList.add(new EditToolModel(R.drawable.picture, EditToolType.PICTURE,"Picture"));
        toolsList.add(new EditToolModel(R.drawable.signature, EditToolType.SIGNATURE,"Signature"));
        toolsList.add(new EditToolModel(R.drawable.watermark, EditToolType.WATERMARK,"Watermark"));
        toolsList.add(new EditToolModel(R.drawable.text, EditToolType.TEXT,"Text"));
        toolsList.add(new EditToolModel(R.drawable.overlay, EditToolType.OVERLAY,"Overlay"));
        toolsList.add(new EditToolModel(R.drawable.color_effect, EditToolType.COLOREFFECT,"Color Effect"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_tools_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.iv_toolIcon.setImageResource(toolsList.get(i).getTool_icon());
        viewHolder.txtIconName.setText(toolsList.get(i).getIcon_name());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onToolSelected.onToolSelected(((EditToolModel) toolsList.get(i)).getEditToolType());
            }
        });
    }

    @Override
    public int getItemCount() {
        return toolsList.size();
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
