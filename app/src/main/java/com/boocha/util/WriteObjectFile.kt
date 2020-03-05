package com.boocha.util

import android.content.Context
import android.util.Log
import java.io.*


class WriteObjectFile(private val parent: Context) {
    companion object {
        const val FILE_USER = "file_user"
    }

    private var fileIn: FileInputStream? = null
    private var fileOut: FileOutputStream? = null
    private var objectIn: ObjectInputStream? = null
    private var objectOut: ObjectOutputStream? = null
    private var outputObject: Any? = null
    private var filePath: String? = null

    fun readObject(fileName: String): Any? {
        try {
            filePath = parent.applicationContext.filesDir.absolutePath + "/" + fileName
            fileIn = FileInputStream(filePath)
            objectIn = ObjectInputStream(fileIn)
            outputObject = objectIn!!.readObject()
            Log.i(FILE_USER, "READIED")

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            if (objectIn != null) {
                try {
                    objectIn!!.close()
                    Log.i("FILE", "USER_READIED")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return outputObject
    }

    fun writeObject(inputObject: Any, fileName: String) {
        try {
            filePath = parent.applicationContext.filesDir.absolutePath + "/" + fileName
            fileOut = FileOutputStream(filePath)
            objectOut = ObjectOutputStream(fileOut)
            objectOut?.writeObject(inputObject)
            fileOut?.fd?.sync()
            Log.i(FILE_USER, "UPDATED")
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (objectOut != null) {
                try {
                    objectOut?.close()
                    Log.i("FILE", "USER_UPDATED")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun deleteObject(fileName: String) {
        try {
            filePath = parent.applicationContext.filesDir.absolutePath + "/" + fileName
            File(filePath).delete()
            Log.i(FILE_USER, "DELETED")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}