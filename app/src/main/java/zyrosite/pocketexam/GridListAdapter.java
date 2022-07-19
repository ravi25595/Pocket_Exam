package zyrosite.pocketexam;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

class GridListAdapter extends BaseAdapter {
    private List<QuestionModel> QUESTIONS;
    private List<String> ANSWERS;
    private int[] ANSWER_TYPE;
    private ItemSelectedListener itemSelectedListener;
    public GridListAdapter(List<QuestionModel> questions, List<String> answers) {
        QUESTIONS = questions;
        ANSWERS = answers;
        ANSWER_TYPE = new int[answers.size()];
        for (int i=0; i<answers.size(); i++){
            ANSWER_TYPE[i] = 0;
        }
    }

    @Override
    public int getCount() {
        return ANSWERS.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        /*
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_item, parent, false);
            TextView name = convertView.findViewById(R.id.textview);
            name.setText("" + (position + 1));
            convertView.setBackgroundResource(R.drawable.circle);
            if (ANSWERS.get(position).isEmpty()) {
                convertView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            } else if (QUESTIONS.get(position).getCorrectAns().equals(ANSWERS.get(position))) {
                convertView.setBackgroundTintList(ColorStateList.valueOf(parent.getContext().getResources().getColor(R.color.successGreen)));
            }else {
                convertView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
            }
        }
         */
        if (convertView == null){
            TextView no = new TextView(parent.getContext());
            no.setText("" + (position + 1));
            no.setBackgroundResource(R.drawable.circle);
            no.setTextSize(22);
            no.setLayoutParams(new GridView.LayoutParams(100, 100));
            no.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            if (ANSWERS.get(position).isEmpty()) {
                no.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            } else if (QUESTIONS.get(position).getCorrectAns().equals(ANSWERS.get(position))) {
                no.setBackgroundTintList(ColorStateList.valueOf(parent.getContext().getResources().getColor(R.color.successGreen)));
                ANSWER_TYPE[position] = 1;
            }else {
                no.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                ANSWER_TYPE[position] = 2;
            }
            convertView = no;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemSelectedListener.onItemSelected(position);
                }
            });
        }

        return convertView;
    }

    public void setItemSelectedListener(ItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public int[] getAnswerType() {
        return ANSWER_TYPE;
    }

    public interface ItemSelectedListener{
        void onItemSelected(int position);
    }
}
