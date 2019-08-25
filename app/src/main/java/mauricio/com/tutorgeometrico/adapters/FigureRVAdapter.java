package mauricio.com.tutorgeometrico.adapters;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import mauricio.com.tutorgeometrico.R;
import mauricio.com.tutorgeometrico.data.DatabaseDescription;
import mauricio.com.tutorgeometrico.interfaces.FigureClickListener;
import mauricio.com.tutorgeometrico.interfaces.FigureLongClickListener;
import mauricio.com.tutorgeometrico.models.Figure;
import mauricio.com.tutorgeometrico.utils.ImageUtils;

public class FigureRVAdapter extends RecyclerView.Adapter<FigureRVAdapter.FigureViewHolder> {

    // ViewHolder
    public class FigureViewHolder extends RecyclerView.ViewHolder{

        private String figCode;
        private long figId;

        public final AppCompatImageView figureImg;
        public final AppCompatTextView figureName;
        public final AppCompatTextView figureDescription;

        public FigureViewHolder(View itemView){

            super(itemView);

            figureImg = itemView.findViewById(R.id.figure_item_list_img);
            figureName = itemView.findViewById(R.id.figure_item_list_name);
            figureDescription = itemView.findViewById(R.id.figure_item_list_description);

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        clickListener.onClick(DatabaseDescription.FigureDesc.buildFigureUri(figId));
                    }
                });
            }

            if (longClickListener != null){

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        selectedItemPosition = getAdapterPosition();
                        notifyDataSetChanged();

                        return longClickListener.onLongClick(DatabaseDescription.FigureDesc.buildFigureUri(figId));

                    }
                });
            }
        }

        public void setFigure(Figure figure){

            figureName.setText(figure.getName());
            figureDescription.setText(figure.getDescription());
        }

        public void setFigCode(String figCode) {
            this.figCode = figCode;
        }

        public String getFigCode() {
            return figCode;
        }

        public void setFigId(long figId) {
            this.figId = figId;
        }
    }

    /* Super class (FigureRVAdapter) definitions */

    private Cursor cursor = null;
    private List<Figure> figures = null;
    private final FigureClickListener clickListener;
    private final FigureLongClickListener longClickListener;

    public int selectedItemPosition = -1;


    public  FigureRVAdapter(@NonNull List<Figure> figures){

        this.clickListener = null;
        this.longClickListener = null;

        this.figures = figures;

    }

    public FigureRVAdapter(@NonNull FigureClickListener clickListener){

        this.clickListener = clickListener;
        this.longClickListener = null;
    }

    public  FigureRVAdapter(FigureClickListener clickListener, FigureLongClickListener longClickListener){

        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @Override
    public FigureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_rv_list_item, parent, false);

        return new FigureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FigureViewHolder holder, int position) {

        if (cursor != null) {

            cursor.moveToPosition(position);

            String code = cursor.getString(cursor.getColumnIndex(DatabaseDescription.FigureDesc.COLUMN_CODE));
            long id = cursor.getLong(cursor.getColumnIndex(DatabaseDescription.FigureDesc._ID));

            holder.setFigCode(code);
            holder.setFigId(id);

          //  byte[] figBytes = cursor.getBlob(cursor.getColumnIndex(DatabaseDescription.FigureDesc.COLUMN_FIG_BYTES));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseDescription.FigureDesc.COLUMN_NAME));
            String desc = cursor.getString(cursor.getColumnIndex(DatabaseDescription.FigureDesc.COLUMN_DESCRIPTION));

            //Bitmap figure = ImageUtils.bytesToBitmap(figBytes);

            holder.figureName.setText(name);
            holder.figureDescription.setText(desc);
            //holder.figureImg.setImageBitmap(figure);

        }else if(figures != null){

            holder.setFigure(figures.get(position));

        }


        holder.itemView.setBackgroundColor(selectedItemPosition == position ? Color.GRAY : Color.WHITE);
    }

    public void setFigures(@NonNull List<Figure> figures){

        this.figures = figures;
        notifyDataSetChanged();
    }

    public void clearData(){

        if (cursor != null){
            cursor = null;
        }

        if (figures != null){

            figures.clear();
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (cursor != null)
            return  cursor.getCount();

        if (figures != null)
            return figures.size();

        return 0;
    }

    public void swapCursor(Cursor cursor){

        this.cursor = cursor;
        notifyDataSetChanged();

    }

    public List<Figure> getFigures(){
        return figures;
    }
}
