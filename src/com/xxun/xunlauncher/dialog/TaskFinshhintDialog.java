package com.xxun.xunlauncher.dialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import com.xxun.xunlauncher.R;

public class TaskFinshhintDialog extends Dialog {
    private TextView tv_task_name,get_gold;
    private String taskName,getgold;

    public TaskFinshhintDialog(Context context,String taskName,String getgold) {
        super(context, R.style.MyDialogStyle);
        this.taskName = taskName;
        this.getgold = getgold;
    }

    public TaskFinshhintDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_finsh);
        initview();

    }

    private void initview() {
        tv_task_name = (TextView)findViewById(R.id.tv_task_name);
        get_gold = (TextView)findViewById(R.id.get_gold);

        tv_task_name.setText(taskName);
        get_gold.setText("+"+getgold+"金币");
    }
}
