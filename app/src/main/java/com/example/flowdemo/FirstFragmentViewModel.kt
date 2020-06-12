package com.example.flowdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class FirstFragmentViewModel : ViewModel() {

    val first = MutableLiveData<String>()
    var firstHit = 0
    val firstCount = MutableLiveData("0")
    val last = MutableLiveData<String>()
    var lastHit = 0
    val lastcount = MutableLiveData("0")
    val age = MutableLiveData<String>()
    var ageHit = 0
    val ageCount = MutableLiveData("0")
    var combineHit = 0
    val combineCount = MutableLiveData("0")

    // much like a text change watcher this is going to be ran every time the text has a change.
    val firstFlow = first.asFlow().flowOn(Dispatchers.Default)
        .mapLatest {
        firstHit += 1
        firstCount.value = (firstHit.toString())
        it
    }

    // error flow for first name needing to be at least two characters
    val firstErrorFlow = first.asFlow().mapLatest {
        if(it.length < 2)
            "name to short"
        else if(it.contains(Regex("\\d")))
            "who has a digit in their name, no"
        else
            null
    }.asLiveData()

    // maybe we don't want to constantly call a flow, most people don't have a single character last name
    // adding debounce allows us to wait a second so they finish typing
    val lastFlow = last.asFlow().debounce(1000).mapLatest {
        lastHit += 1
        lastcount.postValue(lastHit.toString())
        it
    }

    // you should be able to type your age faster than your last name so we only give them a .25 second delay
    val ageFlow = age.asFlow().debounce(250).mapLatest {
        ageHit += 1
        ageCount.value = (ageHit.toString())
        it
    }

    //we want to make sure all the data has been entered before we create the person
    // this combines the top flows into a single one
    val combineFlow: Flow<PersonClass?> = combine(flow = firstFlow, flow2 = lastFlow, flow3 = ageFlow) {
        personFirst: String?, personLast: String?, personAge: String? ->
        combineHit += 1
        combineCount.value = (combineHit.toString())
        val data = PersonClass(personFirst, personLast, personAge, firstCount.value.toString(), lastcount.value.toString(),ageCount.value.toString(), combineCount.value.toString())
        if(data.isDataMissing())
        null
        else
        data

    }.mapLatest {
        it
    }
    val personOneData = combineFlow.asLiveData()
    val cloneData = combineFlow.asLiveData()

}

data class PersonClass(val first: String?, val last: String?, val age: String?, val firstCount: String, val lastCount: String, val ageCount: String, val combineCount: String) {
    override fun toString(): String {
        return "my name is $first $last. I am $age old. first count: $firstCount last count: $lastCount age count: $ageCount \n combine count: $combineCount"
    }
    fun isDataMissing() = first.isNullOrBlank() || last.isNullOrBlank() || age.isNullOrBlank()
}