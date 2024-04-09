package com.example.diplomaapplication.recycler_views.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R
import com.example.diplomaapplication.model.MedicineFormCard
import com.example.diplomaapplication.recycler_views.holder.MedicineFormsViewHolder
import com.example.diplomaapplication.views.medicines.MedicineFormInterface


class MedicineFormsRecyclerViewAdapter(private val listOfMedicinesForms : ArrayList<MedicineFormCard>, private val listener: MedicineFormInterface):RecyclerView.Adapter<MedicineFormsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineFormsViewHolder {
        return MedicineFormsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.photo_and_desc_card,parent,false))
    }

    override fun getItemCount(): Int {
        return listOfMedicinesForms.size
    }

    override fun onBindViewHolder(holder: MedicineFormsViewHolder, position: Int) {
        holder.description.text = listOfMedicinesForms[holder.adapterPosition].title
        holder.photo.setImageResource(listOfMedicinesForms[holder.adapterPosition].photo)

        holder.allBox.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context,if(listOfMedicinesForms[holder.adapterPosition].isChoose) R.color.colorPrimary else R.color.backgroundLightGray)
        holder.description.setTextColor(ContextCompat.getColor(holder.itemView.context,if(listOfMedicinesForms[holder.adapterPosition].isChoose) R.color.white else R.color.darkGray))

        holder.allBox.setOnClickListener {
            listener.changeForm(listOfMedicinesForms[holder.adapterPosition])
            pillFormClick(holder.adapterPosition)
        }
    }

    private fun pillFormClick(index:Int){
        listOfMedicinesForms.forEach { it.isChoose = false }
        listOfMedicinesForms[index].isChoose = true
        notifyDataSetChanged()
    }
}