package com.susion.rabbit.storage

import io.realm.Realm
import io.realm.RealmModel
import io.realm.Sort

/**
 * susionwang at 2019-10-21
 *
 * realm api wrapper
 */
object RabbitRealmHelper {

    fun <T : RealmModel> getDataCount(clazz: Class<T>): Long {
        return getRealm().where(clazz).count()
    }

    fun <T : RealmModel> deleteFirstData(clazz: Class<T>, count: Long, sortFiled: String) {
        val deleteDatas = getRealm().where(clazz).sort(sortFiled).limit(count).findAll()
        getRealm().executeTransaction {
            deleteDatas.deleteAllFromRealm()
        }
    }

    fun saveObj(obj: RealmModel) {
        getRealm().executeTransaction {
            getRealm().insert(obj)
        }
    }

    fun <T : RealmModel> getAllDatas(clazz: Class<T>, results: (datas: List<T>) -> Unit) {
        getRealm().where(clazz).findAllAsync().addChangeListener(results)
    }

    fun <T : RealmModel> getAllDataWithDescendingSort(clazz: Class<T>, sortField:String):List<T> {
      val results = getRealm().where(clazz).findAll().sort(sortField, Sort.DESCENDING)
        return getRealm().copyFromRealm(results)
    }

    private fun getRealm() = Realm.getDefaultInstance()

}