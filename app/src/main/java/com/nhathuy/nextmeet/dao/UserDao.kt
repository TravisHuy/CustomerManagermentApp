package com.nhathuy.nextmeet.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nhathuy.nextmeet.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun register(user: User) : Long

    @Query("SELECT * FROM users WHERE phone = :phone AND password = :password")
    suspend fun login(phone: String, password: String): User?

    @Update
    suspend fun updateUser(user: User)

//    @Query("SELECT * FROM users WHERE is_logged_in = 1 LIMIT 1")
//    fun getCurrentUser(): LiveData<User?>
//
//    @Query("UPDATE users SET is_logged_in=0 WHERE is_logged_in=1")
//    suspend fun logout()

    @Query("UPDATE users SET password= :newPassword WHERE phone= :phone")
    suspend fun updatePassword(phone: String, newPassword: String) : Int

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Int): LiveData<User?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserByIdSuspend(userId: Int): User?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE phone = :phone)")
    suspend fun isPhoneRegistered(phone: String): Boolean
}