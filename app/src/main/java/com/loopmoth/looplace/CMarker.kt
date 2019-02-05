
package com.loopmoth.looplace

class CMarker {
    var id: String? = null
    var title: String? = null
    var snippet: String? = null
    var tag: Any? = null
    var latitude: Double? = null
    var longitude: Double? = null

    constructor()

    constructor(id: String?, title: String?, snippet: String?, tag: Any?, latitude: Double?, longitude: Double?){
        this.id=id
        this.title=title
        this.snippet=snippet
        this.tag=tag
        this.latitude=latitude
        this.longitude=longitude
    }

}