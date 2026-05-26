package ru.mobileup.template.core.map

enum class MapTheme(val yandexMapStyleJson: String) {

    Bright(
        yandexMapStyleJson = """
            [
              {
                "types": ["polyline", "polygon", "point"],
                "tags": {"none": []},
                "stylers": {"saturation": 1}
              }
            ]
        """.trimIndent()
    ),

    Default(
        yandexMapStyleJson = """
            [
              {"featureType":"all","stylers":[{"saturation":0}]}
            ]
        """.trimIndent()
    ),

    Dark(
        yandexMapStyleJson = """
            [
              {"elements": ["geometry"], "stylers": {"color": "#1e1e1e"}},
              {"elements": ["geometry.fill"], "stylers": {"color": "#2b2b2b"}},
              {"elements": ["geometry.outline"], "stylers": {"color": "#3a3a3a"}},
              {"elements": ["label.text.fill"], "stylers": {"color": "#aaaaaa"}},
              {"elements": ["label.text.outline"], "stylers": {"color": "#000000", "opacity": 0.8}},
              {"tags": {"any": ["road"]}, "elements": ["geometry"], "stylers": {"color": "#444444"}},
              {"tags": {"any": ["road_minor", "road_unclassified", "path"]}, "stylers": {"visibility": "off"}},
              {"tags": {"any": ["land"]}, "stylers": {"color": "#1a1a1a"}},
              {"tags": {"any": ["vegetation", "park", "national_park"]}, "stylers": {"color": "#1f3f1f"}},
              {"tags": {"any": ["water"]}, "stylers": {"color": "#0f1a2b"}},
              {"tags": {"any": ["building"]}, "stylers": {"color": "#333333"}},
              {"tags": {"any": ["poi", "transit"]}, "stylers": {"visibility": "off"}},
              {"elements": ["label.icon"], "stylers": {"visibility": "off"}}
            ]
        """.trimIndent()
    )
}
