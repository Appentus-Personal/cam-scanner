package com.app.camscan.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.camscan.R
import com.google.android.material.tabs.TabLayout
import com.app.camscan.adapter.AllGroupAdapter
import com.app.camscan.db.DBHelper
import com.app.camscan.models.DBModel
import java.util.*

class DemoActivity : AppCompatActivity() {

    lateinit var editText: EditText
    lateinit var img_cross: ImageView
    lateinit var tag_tabs: TabLayout
    lateinit var rv_group: RecyclerView

    var dbHelper: DBHelper? = null
    var groupList = ArrayList<DBModel>()

    lateinit var allGroupAdapter: AllGroupAdapter

    var tabList = arrayOf("All Documents", "Documents", "ID Card", "Books", "Personal Tag")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)


        dbHelper = DBHelper(this)

        tag_tabs = findViewById(R.id.tag_tabs)
        rv_group = findViewById(R.id.rv_group)

        editText = findViewById(R.id.et_search)
        img_cross = findViewById(R.id.iv_clear_txt)
//        setTab()

    }
}



   /* private fun setTab(){


        for (text in tabList) {
            val tabLayout: TabLayout = tag_tabs
            tabLayout.addTab(tabLayout.newTab().setText(text as CharSequence))
        }

        for (i in 0 until tag_tabs.getTabCount()) {
            val tab = (tag_tabs.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(0, 0, 30, 0)
            tab.requestLayout()
        }

        Constant.current_tag = "All Docs"

        tag_tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {}

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabSelected(tab: TabLayout.Tab) {


                Constant.current_tag = tabList[tab.position]

                setAllGroupAdapter().execute(*arrayOfNulls<String>(0))
            }
        } as OnTabSelectedListener)


    }


    fun filter(str: String) {
        val arrayList: ArrayList<DBModel> = ArrayList<DBModel>()
        val it: Iterator<DBModel> = groupList.iterator()
        while (it.hasNext()) {
            val next = it.next()
            if (next.getGroup_name().toLowerCase().contains(str.toLowerCase())) {
                arrayList.add(next)
            }
        }
        allGroupAdapter.filterList(arrayList)
    }


    class setAllGroupAdapter : AsyncTask<String?, Void?, String?>() {



        private lateinit var dbHelper: DBHelper



        var groupList = ArrayList<DBModel>()

        var progressDialog: ProgressDialog? = null

        public override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(this)
            progressDialog!!.isIndeterminate = true
            progressDialog!!.setMessage("Loading Data...")
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
        }


        override fun doInBackground(vararg params: String?): String? {
            return if (Constant.current_tag == "All Documents") {
                groupList = dbHelper.getAllGroups()
                null
            } else if (Constant.current_tag == "Documents") {
                groupList = dbHelper.getGroupsByTag("Documents")
                null
            } else if (Constant.current_tag == "ID Card") {
                groupList = dbHelper.getGroupsByTag("ID Card")
                null
            } else if (Constant.current_tag == "Books") {
                groupList = dbHelper.getGroupsByTag("Books")
                null
            } else if (Constant.current_tag == "Personal Tag") {
                groupList = dbHelper.getGroupsByTag("Personal Tag")
                null
            } else {
                groupList = dbHelper.getAllGroups()
                null
            }
        }

        public override fun onPostExecute(str: String?) {
            super.onPostExecute(str)

            val selected_sorting: String? = null


            if (groupList.size > 0) {
                .setVisibility(View.VISIBLE)
                ly_empty.setVisibility(View.GONE)
                MainActivity.mainActivity.selected_sorting = MainActivity.mainActivity.preferences.getString("sortBy", Constant.descending_date)
                if (selected_sorting == Constant.ascending_date) {


                } else if (selected_sorting == Constant.descending_date) {
                    Collections.reverse(groupList)
                } else if (selected_sorting == Constant.ascending_name) {
                    Collections.sort(groupList, SortByName())
                } else if (selected_sorting == Constant.descending_name) {
                    Collections.sort(groupList, SortByName())
                }
                MainActivity.mainActivity.current_mode = MainActivity.mainActivity.preferences.getString("ViewMode", "List")
                if (current_mode == "Grid") {
                    MainActivity.mainActivity.layoutManager = GridLayoutManager(MainActivity.mainActivity as Context, 2, RecyclerView.VERTICAL, false)
                } else {
                    MainActivity.mainActivity.layoutManager = LinearLayoutManager(MainActivity.mainActivity, RecyclerView.VERTICAL, false)
                }
                rv_group.setHasFixedSize(true)
                rv_group.setLayoutManager(layoutManager)
                MainActivity.mainActivity.allGroupAdapter = AllGroupAdapter(MainActivity.mainActivity, MainActivity.mainActivity.groupList, current_mode)
                rv_group.setAdapter(allGroupAdapter)
            } else {
                MainActivity.mainActivity.selected_sorting = MainActivity.mainActivity.preferences.getString("sortBy", Constant.descending_date)
                rv_group.setVisibility(View.GONE)
                ly_empty.setVisibility(View.VISIBLE)

                if (Constant.current_tag == "All Documents") {
                    tv_empty.setText(getResources().getString(R.string.all_docs_empty))
                } else if (Constant.current_tag == "Documents") {
                    tv_empty.setText(getResources().getString(R.string.business_card_empty))
                } else if (Constant.current_tag == "ID Card") {
                    tv_empty.setText(getResources().getString(R.string.id_card_empty))
                } else if (Constant.current_tag == "Books") {
                    tv_empty.setText(getResources().getString(R.string.academic_docs_empty))
                } else if (Constant.current_tag == "Personal Tag") {
                    tv_empty.setText(getResources().getString(R.string.personal_tag_empty))
                } else {
                    tv_empty.setText(getResources().getString(R.string.all_docs_empty))
                }
            }
            progressDialog!!.dismiss()
        }
    }*/



