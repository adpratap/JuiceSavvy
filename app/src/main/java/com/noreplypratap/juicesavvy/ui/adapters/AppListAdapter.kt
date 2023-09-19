package com.noreplypratap.juicesavvy.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noreplypratap.juicesavvy.R
import com.noreplypratap.juicesavvy.models.AppUsageData

class AppListAdapter(private var listOfAppScreenUsage : List<AppUsageData>) : RecyclerView.Adapter<AppListAdapter.ViewHolder>()  {


    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var appName: TextView
        var tvTime: TextView
        var tvPackageName: TextView
        var icon: ImageView
        init {
            tvTime = itemView.findViewById(R.id.tvTotalTime)
            tvPackageName = itemView.findViewById(R.id.tvPackageName)
            appName = itemView.findViewById(R.id.tvAppName)
            icon = itemView.findViewById(R.id.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.app_item,parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val appScreenUsage  = listOfAppScreenUsage[position]

        holder.itemView.apply {
            holder.tvTime.text = "Total Time Visible : ${appScreenUsage.duration}"
            holder.tvPackageName.text = "Package Name : ${appScreenUsage.packageName}"
            holder.appName.text = appScreenUsage.appName
            holder.icon.setImageDrawable(appScreenUsage.icon)
        }
    }

    override fun getItemCount(): Int {
       return listOfAppScreenUsage.size
    }

}