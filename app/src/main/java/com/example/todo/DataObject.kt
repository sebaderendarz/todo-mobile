package com.example.todo


object DataObject {
    var listdata = mutableListOf<CardInfo>()

    fun setData(title: String, description: String) {
        listdata.add(CardInfo(title, description))
    }

    fun getAllData(): List<CardInfo> {
        return listdata
    }

    fun getData(pos:Int): CardInfo {
        return listdata[pos]
    }

    fun deleteData(pos:Int){
        listdata.removeAt(pos)
    }

    fun updateData(pos:Int, title:String, description:String)
    {
        listdata[pos].title=title
        listdata[pos].description=description
    }

}