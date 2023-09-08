package com.noreplypratap.juicesavvy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noreplypratap.juicesavvy.models.AppScreenUsage

class AppListAdapter(private var listOfAppScreenUsage : MutableList<AppScreenUsage>) : RecyclerView.Adapter<AppListAdapter.ViewHolder>()  {


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

            holder.textView.text = "Total Time : ${appScreenUsage.duration}"
            holder.appName.text = "App Name : ${appScreenUsage.appName}"
            holder.icon.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
       return listOfAppScreenUsage.size
    }

}