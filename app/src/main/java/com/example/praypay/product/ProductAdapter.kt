package com.example.praypay.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.praypay.R
import com.example.praypay.databinding.ProductItemBinding

class ProductAdapter: RecyclerView.Adapter<ProductAdapter.ProductHolder>() {
    val productList = ArrayList<Product>()
    class ProductHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = ProductItemBinding.bind(item)

        fun bind(product: Product) = with(binding){
            ivCartProductIcon.setImageResource(R.drawable.placeholder)
            tvCartTitle.text = product.title
            val priceCntString = "${product.price} руб. X ${product.quantity} шт."
            tvCartPriceCnt.text = priceCntString
            val total = "= ${product.price * product.quantity} руб."
            tvCartSum.text = total.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductHolder(view)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun addProduct(product: Product) {
        productList.add(product)
        notifyDataSetChanged()
    }

    fun clear(){
        productList.clear()
        notifyDataSetChanged()
    }


}