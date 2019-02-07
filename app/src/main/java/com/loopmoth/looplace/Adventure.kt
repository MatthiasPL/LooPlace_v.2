package com.loopmoth.looplace

import com.google.android.gms.maps.model.Marker

class Adventure {
    var name: String? = null
    var description: String? =null

    var markers: List<CMarker>? = null

    var key: String? = null

    constructor()
    constructor(name: String?, description: String?, markers: List<CMarker>,key: String?){
        this.name = name
        this.description = description
        this.markers = markers
        this.key = key
    }
}