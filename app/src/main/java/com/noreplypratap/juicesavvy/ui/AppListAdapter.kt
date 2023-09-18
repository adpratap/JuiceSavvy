package com.noreplypratap.juicesavvy.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noreplypratap.juicesavvy.R
import com.noreplypratap.juicesavvy.models.AppUsageData

class AppListAdapter(private var listOfAppScreenUsage : MutableList<AppUsageData>) : RecyclerView.Adapter<AppListAdapter.ViewHolder>()  {


    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var appName: TextView
        var textView: TextView
        var icon: ImageView
        init {
            textView = itemView.findViewById(R.id.tvAppData)
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
            holder.textView.text = "Total Time Visible: ${appScreenUsage.duration}"
            holder.appName.text = appScreenUsage.appName
            holder.icon.setImageDrawable(appScreenUsage.icon)
        }
    }

    override fun getItemCount(): Int {
       return listOfAppScreenUsage.size
    }

}