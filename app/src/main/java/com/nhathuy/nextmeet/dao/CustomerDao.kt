package com.nhathuy.nextmeet.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nhathuy.nextmeet.model.Customer
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun register(customer: Customer)

    @Query("select * from customers")
    fun  getAllCustomer():Flow<List<Customer>>

    @Update
    suspend fun editCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("select * from customers where id= :customerId")
    fun getCustomerById(customerId:Int) : Customer?

    @Query("delete from customers")

    suspend fun deleteAllCustomer()
}