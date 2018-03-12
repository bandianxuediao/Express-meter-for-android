package cc.lifelink.cn;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class City_cnActivity extends Activity implements OnClickListener {
	Stack<String> optr = new Stack<String>();
	Stack<Double> opnd = new Stack<Double>();
	/** Called when the activity is first created. */

	TextView weigh = null;
	TextView total_price = null;
	int index_begin = -1;
	boolean press_answer_flag = false;
	private DBManager dbm;
	private SQLiteDatabase db;
	private Spinner spinner1 = null;
	private String province = null;
	private String m_start = "河南";
	private String procode = null;
	private int decimal = 0, point_flag = 0, point_num = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// 设置两个Text的连接ID
		weigh = (TextView) findViewById(R.id.weight);
		total_price = (TextView) findViewById(R.id.total_price);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner1.setPrompt("省");
		// 设置Button变量的ID
		Button num0 = (Button) findViewById(R.id.zero_button);
		Button num1 = (Button) findViewById(R.id.one_button);
		Button num2 = (Button) findViewById(R.id.two_button);
		Button num3 = (Button) findViewById(R.id.three_button);
		Button num4 = (Button) findViewById(R.id.four_button);
		Button num5 = (Button) findViewById(R.id.five_button);
		Button num6 = (Button) findViewById(R.id.six_button);
		Button num7 = (Button) findViewById(R.id.seven_button);
		Button num8 = (Button) findViewById(R.id.eight_button);
		Button num9 = (Button) findViewById(R.id.nine_button);
		Button point = (Button) findViewById(R.id.point_button);

		Button clear = (Button) findViewById(R.id.clear_button);
		Button calc = (Button) findViewById(R.id.enter_button);

		// 设置按键监听事件（链接到上面的ID）
		num0.setOnClickListener(this);
		num1.setOnClickListener(this);
		num2.setOnClickListener(this);
		num3.setOnClickListener(this);
		num4.setOnClickListener(this);
		num5.setOnClickListener(this);
		num6.setOnClickListener(this);
		num7.setOnClickListener(this);
		num8.setOnClickListener(this);
		num9.setOnClickListener(this);
		point.setOnClickListener(this);
		clear.setOnClickListener(this);
		calc.setOnClickListener(this);
		new AlertDialog.Builder(this).setTitle("安能快递计价").setMessage("数据仅适用于中牟")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						initSpinner1();

					}
				}).show();

	}

	public void initSpinner1() {
		dbm = new DBManager(this);
		dbm.openDatabase();
		db = dbm.getDatabase();
		List<MyListItem> list = new ArrayList<MyListItem>();

		try {
			String sql = "select * from province";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("code"));
				byte bytes[] = cursor.getBlob(2);
				String name = new String(bytes, "gbk");
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("code"));
			byte bytes[] = cursor.getBlob(2);
			String name = new String(bytes, "gbk");
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

		} catch (Exception e) {
		}
		dbm.closeDatabase();
		db.close();

		MyAdapter myAdapter = new MyAdapter(this, list);
		spinner1.setAdapter(myAdapter);
		spinner1.setSelection(15, true);
		province = "河南";
		procode = "河南";
		Toast.makeText(City_cnActivity.this, "您选择了" + province,
				Toast.LENGTH_LONG).show();
		spinner1.setOnItemSelectedListener(new SpinnerOnSelectedListener1());
	}

	class SpinnerOnSelectedListener1 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			province = ((MyListItem) adapterView.getItemAtPosition(position))
					.getName();
			String pcode = ((MyListItem) adapterView
					.getItemAtPosition(position)).getPcode();
			Toast.makeText(City_cnActivity.this, "您选择了" + province,
					Toast.LENGTH_LONG).show();
			procode = pcode;
			weigh.setText("");
			decimal = 0;
			point_flag = 0;
			point_num = 0;
			index_begin = -1;
			press_answer_flag = false;
			total_price.setText(pcode);
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Button btn = (Button) arg0;
		String show = weigh.getText().toString();
		String temp = null;

		double in_weigh = 0;
		double out_price = 0;
		if (btn.getText().equals(".")) {
			if (press_answer_flag) {
				weigh.setText("");
				total_price.setText("");
				optr.clear();
				opnd.clear();
				index_begin = -1;
				press_answer_flag = false;
				show = "";
			}
			if ((decimal == 1) && (point_num < 1)) {
				show += btn.getText();
				weigh.setText(show);
				point_num++;
				point_flag = 1;
				if (index_begin == -1) {
					index_begin = show.lastIndexOf(btn.getText().toString());
				}
			}
		}
		if (btn.getText().equals("0") || btn.getText().equals("1")
				|| btn.getText().equals("2") || btn.getText().equals("3")
				|| btn.getText().equals("4") || btn.getText().equals("5")
				|| btn.getText().equals("6") || btn.getText().equals("7")
				|| btn.getText().equals("8") || btn.getText().equals("9")) {
			if (press_answer_flag) {
				weigh.setText("");
				total_price.setText("");
				optr.clear();
				opnd.clear();
				index_begin = -1;
				press_answer_flag = false;
				show = "";
			}
			decimal = 1;// 可以开始输入小数
			if (point_flag > 0) {
				point_flag++;
			}
			if (point_flag <= 3) {
				show += btn.getText();
				weigh.setText(show);
				if (index_begin == -1) {
					index_begin = show.lastIndexOf(btn.getText().toString());
					// Toast.makeText(this,
					// "index_begin is changed from -1 to "+index_begin,
					// Toast.LENGTH_LONG).show();
				}
			}
		} else if (btn.getText().equals("计算总价")) {
			press_answer_flag = true;
			if (index_begin != -1) {
				temp = show.substring(index_begin);
				opnd.push(Double.valueOf(temp).doubleValue());
				in_weigh = Math.ceil(opnd.peek().doubleValue());
				opnd.clear();
				decimal = 0;
				point_flag = 0;
				point_num = 0;
				index_begin = -1;
				// Toast.makeText(this,
				// "temp is"+temp+". push into opnd,the number is "+Double.valueOf(temp).doubleValue(),
				// Toast.LENGTH_LONG).show();
			}
			if (in_weigh > 0) {
				if (m_start == "河南") {
					if (procode.equals("河南")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 3.5;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 4 + (in_weigh - 3) * 0.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 4 + (in_weigh - 3) * 0.8;
					}
					if (procode.equals("重庆")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.8;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.3;
					}
					if (procode.equals("山东")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.0;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.3;
					}
					if (procode.equals("河北")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 4;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 3.5;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.0;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.3;
					}
					if (procode.equals("北京")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.0;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.3;
					}

					if (procode.equals("天津")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.0;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.3;
					}

					if (procode.equals("陕西")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.0;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.3;
					}

					if (procode.equals("山西")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.0;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.5;
					}

					if (procode.equals("湖北")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.0;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.3;
					}

					if (procode.equals("江苏")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.0;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.5;
					}

					if (procode.equals("上海")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.0;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.5;
					}

					if (procode.equals("湖南")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.0;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.5;
					}

					if (procode.equals("浙江")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.3;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.5;
					}

					if (procode.equals("安徽")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.3;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.8;
					}

					if (procode.equals("广东")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.8;
					}

					if (procode.equals("福建")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.8;
					}

					if (procode.equals("四川")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.8;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.8;
					}

					if (procode.equals("江西")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.8;
					}

					if (procode.equals("广西")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 1.8;
					}

					if (procode.equals("贵州")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.8;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 2.0;
					}

					if (procode.equals("辽宁")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.8;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 2.0;
					}

					if (procode.equals("吉林")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 4;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.8;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 2.0;
					}

					if (procode.equals("黑龙江")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 5;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 8 + (in_weigh - 3) * 2.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 8 + (in_weigh - 3) * 2.8;
					}

					if (procode.equals("海南")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 8;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 10;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 8 + (in_weigh - 3) * 2.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 8 + (in_weigh - 3) * 2.8;
					}

					if (procode.equals("甘肃")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 7;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 8;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.8;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 2.0;
					}

					if (procode.equals("青海")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 7;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 8;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 6 + (in_weigh - 3) * 1.8;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 6 + (in_weigh - 3) * 2.0;
					}

					if (procode.equals("云南")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 2.5;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 10;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 8 + (in_weigh - 3) * 2.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 8 + (in_weigh - 3) * 3.0;
					}

					if (procode.equals("西藏")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 7;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 10;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 8 + (in_weigh - 3) * 2.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 8 + (in_weigh - 3) * 3.0;
					}

					if (procode.equals("内蒙古")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 8;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 10;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 8 + (in_weigh - 3) * 2.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 8 + (in_weigh - 3) * 2.5;
					}

					if (procode.equals("宁夏")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 8;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 10;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 8 + (in_weigh - 3) * 2.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 8 + (in_weigh - 3) * 2.5;
					}

					if (procode.equals("新疆")) {
						if ((in_weigh > 0) && (in_weigh <= 3))
							out_price = 8;
						if ((in_weigh > 3) && (in_weigh <= 5))
							out_price = 10;
						if ((in_weigh > 5) && (in_weigh <= 10))
							out_price = 8 + (in_weigh - 3) * 2.5;
						if ((in_weigh > 10) && (in_weigh <= 20))
							out_price = 8 + (in_weigh - 3) * 3.0;
					}

					opnd.push(Double.valueOf(out_price).doubleValue());

				}

				total_price.setText(opnd.peek().toString());

			}

		} else if (btn.getText().equals("C")) {
			weigh.setText("");
			total_price.setText("");
			optr.clear();
			opnd.clear();
			decimal = 0;
			point_flag = 0;
			point_num = 0;
			index_begin = -1;
			press_answer_flag = false;
		}
	}
}