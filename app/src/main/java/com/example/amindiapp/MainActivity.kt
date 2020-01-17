package com.example.amindiapp


import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var CITY: String = "Tbilisi"
    var CITY1: String = "Tbilisi"
    val API: String = "1bca69a96d7d90333819f87cb9402424" // Use your own API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherTask().execute()

    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE

            val Text = findViewById<TextView>(R.id.errorText)
            Text.setOnClickListener {
                CITY = CITY1
                weatherTask().execute()
            }
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/forecast?q=$CITY&units=metric&APPID=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */

                val jsonObj = JSONObject(result)

                val main = jsonObj.getJSONArray("list").getJSONObject(0)

                fun getTemp(Index: Int): String {
                    val wt = jsonObj.getJSONArray("list").getJSONObject(Index).getJSONObject("main").getString("temp")+"°C"
                    return  wt
                }
                fun getImage(Index: Int): Int {
                    val main1 = jsonObj.getJSONArray("list").getJSONObject(Index)
                    val imageType = main1.getJSONArray("weather").getJSONObject(0).getString("main").toString()
                    val dt:Long = jsonObj.getJSONArray("list").getJSONObject(Index).getLong("dt")
                    val Time = SimpleDateFormat("HH", Locale.ENGLISH).format(Date(dt*1000)).toInt()
                    if(imageType == "Clear" && Time > 9 && Time < 20){
                        return  R.drawable.sun
                    }
                    if(imageType == "Clear" && (Time < 9 || Time > 20)){
                        return  R.drawable.moon
                    }
                    if(imageType == "Clouds" && (Time < 9 || Time > 20)){
                        return  R.drawable.mooncloud
                    }
                    if(imageType == "Clouds" && Time > 9 && Time < 20){
                        return  R.drawable.suncloud
                    }
                    if(imageType == "Snow"){
                        return  R.drawable.snow
                    }
                    if(imageType == "Rain" && Time > 9 && Time < 20){
                        return  R.drawable.sunrain
                    }
                    if(imageType == "Rain" && (Time < 9 || Time > 20)){
                        return  R.drawable.moonrain
                    }
                    return  R.drawable.cloud
                }

                fun getTime(Index: Int): String {
                    val dt:Long = jsonObj.getJSONArray("list").getJSONObject(Index).getLong("dt")
                    return "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(dt*1000))
                }
                fun getTime1(Index: Int): String {
                    val dt:Long = jsonObj.getJSONArray("list").getJSONObject(Index).getLong("dt")
                    return SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(dt*1000))
                }
                fun SearchIndex(): Int {
                    val listLength = jsonObj.getJSONArray("list").length()
                    val dt:Long = jsonObj.getJSONArray("list").getJSONObject(0).getLong("dt")
                    val Today = SimpleDateFormat("dd", Locale.ENGLISH).format(Date(dt*1000))
                    for(i in 1 .. listLength){
                        val dt1:Long = jsonObj.getJSONArray("list").getJSONObject(i).getLong("dt")
                        val NextDay = SimpleDateFormat("dd", Locale.ENGLISH).format(Date(dt1*1000))
                        val NextDay12Hour = SimpleDateFormat("hh a", Locale.ENGLISH).format(Date(dt1*1000))
                        if(Today < NextDay && NextDay12Hour.toString().contains("10 AM") ) {
                            return i
                        }
                    }
                    return  -1
                }

                fun getTimeFull(Index: Int): String {
                    val dt:Long = jsonObj.getJSONArray("list").getJSONObject(Index).getLong("dt")
                    return SimpleDateFormat("dd MMMM", Locale.ENGLISH).format(Date(dt*1000))
                }



                //Main Temperature
                val temp = getTemp(0)

                //Description of the Weather
                val weather = main.getJSONArray("weather").getJSONObject(0)
                val weatherDescription = weather.getString("description")

                //Update
                val updatedAtText = getTime(0)

                //City
                val city = jsonObj.getJSONObject("city")
                val address = city.getString("name")
                val country = city.getString("country")

                //Weather Next Hours BY Celsius
                val wt1c = getTemp(1)
                val wt2c = getTemp(2)
                val wt3c = getTemp(3)
                val wt4c = getTemp(4)
                val wt5c = getTemp(5)


                //Time Next Hours BY Hours
                val wt1t = getTime1(1)
                val wt2t = getTime1(2)
                val wt3t = getTime1(3)
                val wt4t = getTime1(4)
                val wt5t = getTime1(5)

                //Next days Date

                val SearchIndex = SearchIndex()

                val ndd1d = getTemp(SearchIndex)
                val ndd2d = getTemp(SearchIndex + 8)
                val ndd3d = getTemp(SearchIndex + 16)
                val ndd4d = getTemp(SearchIndex + 24)

                val ndd1n = getTemp(SearchIndex + 4 )
                val ndd2n = getTemp(SearchIndex + 12)
                val ndd3n = getTemp(SearchIndex + 20)
                val ndd4n = getTemp(SearchIndex + 28)


                val ndd1 = getTimeFull(SearchIndex)
                val ndd2 = getTimeFull(SearchIndex + 8)
                val ndd3 = getTimeFull(SearchIndex + 16)
                val ndd4 = getTimeFull(SearchIndex + 24)
                val ndd5 = getTimeFull(39)
                if(ndd4 == ndd5){
                    val ndd5d = "Not Avalible"
                    val ndd5n = "Not Avalible"
                    val ndd5 = "Not Avalible"

                    findViewById<TextView>(R.id.ndd5d).text =ndd5d
                    findViewById<TextView>(R.id.ndd5n).text =ndd5n
                    findViewById<TextView>(R.id.ndd5).text =ndd5
                }
                if(ndd4 != ndd5){
                    findViewById<TextView>(R.id.ndd5).text =ndd5
                    if(SearchIndex + 32 <= 39){
                        val ndd5d = getTemp(SearchIndex + 32)
                        findViewById<TextView>(R.id.ndd5d).text =ndd5d
                        nd5d.setImageResource(getImage(SearchIndex + 32))
                    }
                    else{
                        val ndd5d = "Not Avalible"
                        findViewById<TextView>(R.id.ndd5d).text =ndd5d
                    }
                    val ndd5n = "Not Avalible"
                    findViewById<TextView>(R.id.ndd5n).text =ndd5n
                }



                /* Populating extracted data into views */
                findViewById<TextView>(R.id.address).text = (address + ", " + country)
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp


                //Weather Next Hours Temperature
                findViewById<TextView>(R.id.wt1c).text = wt1c
                findViewById<TextView>(R.id.wt2c).text = wt2c
                findViewById<TextView>(R.id.wt3c).text = wt3c
                findViewById<TextView>(R.id.wt4c).text = wt4c
                findViewById<TextView>(R.id.wt5c).text = wt5c

                //Weather Next Hours Hours
                findViewById<TextView>(R.id.wt1t).text = wt1t
                findViewById<TextView>(R.id.wt2t).text = wt2t
                findViewById<TextView>(R.id.wt3t).text = wt3t
                findViewById<TextView>(R.id.wt4t).text = wt4t
                findViewById<TextView>(R.id.wt5t).text = wt5t

                //Weather Next Days Day Temperature
                findViewById<TextView>(R.id.ndd1d).text =ndd1d
                findViewById<TextView>(R.id.ndd2d).text =ndd2d
                findViewById<TextView>(R.id.ndd3d).text =ndd3d
                findViewById<TextView>(R.id.ndd4d).text =ndd4d

                //Weather Next Days Night Temperature
                findViewById<TextView>(R.id.ndd1n).text =ndd1n
                findViewById<TextView>(R.id.ndd2n).text =ndd2n
                findViewById<TextView>(R.id.ndd3n).text =ndd3n
                findViewById<TextView>(R.id.ndd4n).text =ndd4n

                findViewById<TextView>(R.id.ndd1).text =ndd1
                findViewById<TextView>(R.id.ndd2).text =ndd2
                findViewById<TextView>(R.id.ndd3).text =ndd3
                findViewById<TextView>(R.id.ndd4).text =ndd4

                MainImage.setImageResource(getImage(0))
                wn1i.setImageResource(getImage(1))
                wn2i.setImageResource(getImage(2))
                wn3i.setImageResource(getImage(3))
                wn4i.setImageResource(getImage(4))
                wn5i.setImageResource(getImage(5))

                nd1d.setImageResource(getImage(SearchIndex))
                nd2d.setImageResource(getImage(SearchIndex + 8))
                nd3d.setImageResource(getImage(SearchIndex + 16))
                nd4d.setImageResource(getImage(SearchIndex + 24))

                nd1n.setImageResource(getImage(SearchIndex + 4))
                nd2n.setImageResource(getImage(SearchIndex + 12))
                nd3n.setImageResource(getImage(SearchIndex + 20))
                nd4n.setImageResource(getImage(SearchIndex + 28))


                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

                val button = findViewById<Button>(R.id.Search)
                button.setOnClickListener {
                    CITY1 = CITY
                    CITY = City.text.toString()
                    weatherTask().execute()
                }

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }

    }
}

