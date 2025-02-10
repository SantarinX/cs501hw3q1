package edu.cs501hw3.p1

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.XmlResourceParser
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import edu.cs501hw3.p1.ui.theme.P1Theme


//Create a photo gallery using LazyVerticalGrid in a staggered layout.
//Store image file names and titles in an XML resource (res/xml/photos.xml).
//Use LazyVerticalGrid with a staggered layout to display images of varying heights.
//You must load images dynamically from the drawable folder.
//When the user clicks an image should enlarge it using a coroutine animation.
//The photos.xml file should be in this format:

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            P1Theme {
                Main(this)
            }
        }
    }
}

data class Photo(val filename:String, val title:String, val id:Int )

@Composable
fun Main(context: Context){
    val photos = photoParser(context)
    PhotoGrid(photos)
}

@SuppressLint("DiscouragedApi")
@Composable
fun photoParser(context: Context):List<Photo>{
    val parser: XmlResourceParser = context.resources.getXml(R.xml.photos)
    val photos = mutableListOf<Photo>()
    while (parser.eventType != XmlResourceParser.END_DOCUMENT){
        if (parser.eventType == XmlResourceParser.START_TAG && parser.name == "photo"){
            val filename = parser.getAttributeValue(null, "filename")
            val title = parser.getAttributeValue(null, "title")
            val id = context.resources.getIdentifier(filename, "drawable", context.packageName)
            photos.add(Photo(filename, title, id))
        }
        parser.next()
    }
    return photos
}

@Composable
fun PhotoGrid(photos: List<Photo>) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(9.dp),
        verticalItemSpacing = 9.dp,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        modifier = Modifier.padding(19.dp)
    ) {
        items(photos.size) { index ->
            EnlargeableImage(photos[index])
        }
    }
}


@Composable
fun EnlargeableImage(photo: Photo) {
    var showFullScreen  by remember { mutableStateOf(false) }
    Box() {
        Image(
            painter = painterResource(id = photo.id),
            contentDescription = photo.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showFullScreen = true }
        )

        if (showFullScreen) {
            Dialog({ showFullScreen = false },) {
                Box(
                    modifier = Modifier
                        .clickable { showFullScreen = false },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = photo.id),
                        contentDescription = "Enlarged ${photo.title}",
                        modifier = Modifier.fillMaxSize(0.95f))
                }
            }
        }
    }
}