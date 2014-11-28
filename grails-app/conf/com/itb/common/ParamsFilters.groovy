package com.itb.common

class ParamsFilters {

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                parseRequest(params)
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }

    /*
    * This method parsing the params.
    * And removing all the unexpecting values  in order to security
    * 1.  First will if params value is type of String then simply calling the  stripXSS and changing current value to returned value
    * 2.  Second if params[Key]  is type of String then iterating the list and calling stripXSS for each list value
    * 3.  Its a special condition. If params is type of map Like [order.orderLineItem:[invoiceText:'', amount:'']]
    * 4. //TODO next will have the method to parse ids in Long and DateString in Date and BooleanString into a Boolean Object
    * */
    def parseRequest(def params){

        Set keys = params.keySet()
        keys.each {String key ->
            if(params[key] instanceof String){
                params[key] = StringUtils.stripXSS(params[key] as String)
            }else if(params[key] instanceof String[]){
//                List oldList = params[key] as List
                params[key] = createListFromParams(params[key])
            }else if(params[key] instanceof Map){
                def newMap = [:]
                def oldMap = params[key]
                Set keysOfMap = params[key].keySet()
                keysOfMap.each {String keyOfMap ->
                    if(oldMap[keyOfMap].class == String[].class){
                        oldMap[keyOfMap] = createListFromParams(oldMap[keyOfMap])
                    }
                    else if (oldMap[keyOfMap]) {
                        oldMap[keyOfMap] = StringUtils.stripXSS(oldMap[keyOfMap] as String)
                    }
                    else
                        oldMap[keyOfMap] = StringUtils.stripXSS(oldMap[keyOfMap] as String)
                }
                params[key] = oldMap
            }
        }
    }

    /*
    * This method get the params in String[] form
    * and then iterate all element of list.
    * Parse all the element by calling stripXSS
    * return modified String[] by removing XSS data.
    * */
    def createListFromParams(String[] oldList) {
        def newList = []
        oldList.each {
            newList << StringUtils.stripXSS(it)
        }
        return newList as String[]
    }
}
