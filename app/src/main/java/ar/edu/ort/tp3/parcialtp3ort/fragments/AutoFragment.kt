package ar.edu.ort.tp3.parcialtp3ort.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.ort.tp3.parcialtp3ort.APIServiceBuilder.APIServiceBuilder
import ar.edu.ort.tp3.parcialtp3ort.Models.AutoViewModel
import ar.edu.ort.tp3.parcialtp3ort.R
import ar.edu.ort.tp3.parcialtp3ort.adapters.CarAdapter
import ar.edu.ort.tp3.parcialtp3ort.Models.CarResponse
import ar.edu.ort.tp3.parcialtp3ort.Models.LoginViewModel
import ar.edu.ort.tp3.parcialtp3ort.Models.LogoResponse
import ar.edu.ort.tp3.parcialtp3ort.entities.Make
import ar.edu.ort.tp3.parcialtp3ort.database.DBHelper
import ar.edu.ort.tp3.parcialtp3ort.database.appDatabase
import ar.edu.ort.tp3.parcialtp3ort.entities.Car
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutoFragment : Fragment() {

    //private lateinit var viewModel: AutoViewModel
    private lateinit var v: View
    private lateinit var carList: RecyclerView
    var marcasList: MutableList<Make> = ArrayList<Make>()
    private lateinit var cars : MutableList<Car>//MutableList<CarResponse>
    private lateinit var viewModel: AutoViewModel
    private lateinit var fireBaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v =  inflater.inflate(R.layout.fragment_auto, container, false)
        carList =  v.findViewById<RecyclerView>(R.id.carListView)
        viewModel = ViewModelProvider(requireActivity()).get(AutoViewModel::class.java)
        cars  = mutableListOf<Car>()//mutableListOf<CarResponse>()
        fireBaseAuth = Firebase.auth
        carList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CarAdapter(cars, context, false, fireBaseAuth.currentUser?.email.toString())
        }
        if(viewModel.campo.value.toString().isNullOrEmpty()) {
            Log.d("VIEWMODEL empty", viewModel.tipoBusqueda.value.toString())
            Log.d("VIEWMODEL empty", viewModel.campo.value.toString())
            getAllCars()
        }else if(viewModel.tipoBusqueda.value.toString()=="gas"){
            Log.d(viewModel.campo.value.toString(),viewModel.tipoBusqueda.value.toString() )
            getGasCars()
        }else if(viewModel.tipoBusqueda.value.toString()=="diesel"){
            Log.d(viewModel.campo.value.toString(),viewModel.tipoBusqueda.value.toString() )
            getDieselCars()
        }else if(viewModel.tipoBusqueda.value.toString()=="electric"){
            Log.d(viewModel.campo.value.toString(),viewModel.tipoBusqueda.value.toString() )
            getElectricCars()
        }else{
            Log.d("VIEWMODEL Else", viewModel.tipoBusqueda.value.toString())
            Log.d("VIEWMODEL Else", viewModel.campo.value.toString())
            if(viewModel.campo.value.toString()=="make"){
                searchCarsByMake(viewModel.tipoBusqueda.value.toString())
            }else if(viewModel.campo.value.toString()=="model"){
                //TODO: Pasar ViewModel con campo Model y tipoDeBusqueda = Search.text
                searchCarsByModel(viewModel.tipoBusqueda.value.toString())
            }else{
                getAllCars()
            }
        }

        return v
    }

    private fun getData(carList: MutableList<Car>?) {//(carList: List<CarResponse>) {
        if (carList != null) {
            for(car in carList) {
                var exists: Boolean = false
                for(marca in marcasList) {
                    val isEqual = marca.name == car.marca
                    if(isEqual) {
                        marca.count++
                        exists = true
                    }
                }
                if(!exists) {
                    marcasList.add(Make(car.marca, car.image,1))
                }
            }
            Log.d("Lista de marcas pre setup", marcasList.size.toString())
            //getLogoImages()
            showData(cars)
        }
    }
    private fun getGasCars(){
        //val mDbHelper = DBHelper.getIntance()//(context,null)
        //val carList : List<CarResponse> = mDbHelper.searchCars("gas","combustible")
        var carList : MutableList<Car>? = appDatabase.getIntance()?.carDao()?.getCarsByCombustible("gas")
        if( carList== null || (carList != null && carList.size <= 2)){
            val service = APIServiceBuilder.createCarService()
            service.getCarsByFuel("gas").enqueue(object: Callback<List<CarResponse>> {
                override fun onResponse(
                    call: Call<List<CarResponse>>,
                    response: Response<List<CarResponse>>
                ) {
                    //mDbHelper.addCars(response.body()!!)
                    //cars.addAll(response.body()!!)
                    //getData(response.body()!!)
                    appDatabase.getIntance()?.carDao()?.insertAll(Car.getCarEntityFromCarResponse(response.body()!!))
                    carList = appDatabase.getIntance()?.carDao()?.getCarsByCombustible("gas")
                    if( !carList.isNullOrEmpty() ){
                        cars.addAll(carList!!)
                        getData(appDatabase.getIntance()?.carDao()?.getCarsByCombustible("gas"))
                    }
                }
                override fun onFailure(call: Call<List<CarResponse>>, t: Throwable) {
                    //Not yet implemented
                }
            })
        }else{
            cars.addAll(carList!!)
            getData(cars)
        }
    }
    private fun getDieselCars(){
        //val mDbHelper = DBHelper.getIntance()//(context,null)
        //val carList : List<CarResponse> = mDbHelper.searchCars("diesel","combustible")
        var carList : MutableList<Car>? = appDatabase.getIntance()?.carDao()?.getCarsByCombustible("diesel")
        if(carList != null && carList.size <= 2){
            val service = APIServiceBuilder.createCarService()
            service.getCarsByFuel("diesel").enqueue(object: Callback<List<CarResponse>> {
                override fun onResponse(
                    call: Call<List<CarResponse>>,
                    response: Response<List<CarResponse>>
                ) {
                    //mDbHelper.addCars(response.body()!!)
                    //cars.addAll(response.body()!!)
                    //getData(response.body()!!)
                    appDatabase.getIntance()?.carDao()?.insertAll(Car.getCarEntityFromCarResponse(response.body()!!))
                    carList = appDatabase.getIntance()?.carDao()?.getCarsByCombustible("diesel")
                    if( !carList.isNullOrEmpty() ){
                        cars.addAll(carList!!)
                        getData(appDatabase.getIntance()?.carDao()?.getCarsByCombustible("diesel"))
                    }
                }
                override fun onFailure(call: Call<List<CarResponse>>, t: Throwable) {
                    //Not yet implemented
                }
            })
        }else{
            cars.addAll(carList!!)
            getData(cars)
        }
    }
    private fun getElectricCars(){
        //val mDbHelper = DBHelper.getIntance()//(context,null)
        // carList : List<CarResponse> = mDbHelper.searchCars("electric","combustible")
        var carList : MutableList<Car>? = appDatabase.getIntance()?.carDao()?.getCarsByCombustible("electric")
        if(carList != null && carList.size <= 2){
            val service = APIServiceBuilder.createCarService()
            service.getCarsByFuel("electricity").enqueue(object: Callback<List<CarResponse>> {
                override fun onResponse(
                    call: Call<List<CarResponse>>,
                    response: Response<List<CarResponse>>
                ) {
                    //mDbHelper.addCars(response.body()!!)
                    //cars.addAll(response.body()!!)
                    //getData(response.body()!!)
                    appDatabase.getIntance()?.carDao()?.insertAll(Car.getCarEntityFromCarResponse(response.body()!!))
                    carList = appDatabase.getIntance()?.carDao()?.getCarsByCombustible("electric")
                    if( !carList.isNullOrEmpty() ){
                        cars.addAll(carList!!)
                        getData(appDatabase.getIntance()?.carDao()?.getCarsByCombustible("electric"))
                    }
                }
                override fun onFailure(call: Call<List<CarResponse>>, t: Throwable) {
                    //Not yet implemented
                }
            })
        }else{
            cars.addAll(carList!!)
            getData(cars)
        }
    }
    private fun searchCarsByMake(manu : String){
        //val mDbHelper = DBHelper.getIntance()//(context,null)
        //val carList : List<CarResponse> = mDbHelper.searchCars(manu,"make")
        var carList : MutableList<Car>? = appDatabase.getIntance()?.carDao()?.getCarsByMarca(manu)
        if(carList != null && carList.size <= 2){
            val service = APIServiceBuilder.createCarService()
            service.getCarsByManufacturer(manu).enqueue(object: Callback<List<CarResponse>> {
                override fun onResponse(
                    call: Call<List<CarResponse>>,
                    response: Response<List<CarResponse>>
                ) {
                    if (response.isSuccessful) {
                        /*Log.d("VIEWMODEL SEARCH MAKE",response.toString())
                        Log.d("1 VIEWMODEL SEARCH MAKE",cars.toString())
                        Log.d("1 VIEWMODEL SEARCH MAKE S",cars.size.toString())
                        //cars  = mutableListOf<CarResponse>()
                        Log.d("2 VIEWMODEL SEARCH MAKE",cars.toString())
                        Log.d("2 VIEWMODEL SEARCH MAKE S",cars.size.toString())
                        //cars.addAll(response.body()!!)
                        Log.d("3 VIEWMODEL SEARCH MAKE",cars.toString())
                        Log.d("3 VIEWMODEL SEARCH MAKE S",cars.size.toString())*/
                        //mDbHelper.addCars(response.body()!!)
                        //getData(response.body()!!)
                        appDatabase.getIntance()?.carDao()?.insertAll(Car.getCarEntityFromCarResponse(response.body()!!))
                        carList = appDatabase.getIntance()?.carDao()?.getCarsByMarca(manu)
                        if( !carList.isNullOrEmpty() ){
                            cars.addAll(carList!!)
                            getData(appDatabase.getIntance()?.carDao()?.getCarsByMarca(manu))
                        }
                    }
                }
                override fun onFailure(call: Call<List<CarResponse>>, t: Throwable) {
                    //Not yet implemented
                }
            })
        }else{
            //cars  = mutableListOf<CarResponse>()
            cars.addAll(carList!!)
            getData(cars)
        }
    }
    private fun searchCarsByModel(model:String){
        //val mDbHelper = DBHelper.getIntance()//(context,null)
        //val carList : List<CarResponse> = mDbHelper.searchCars(model,"model")
        var carList : MutableList<Car>? = appDatabase.getIntance()?.carDao()?.getCarsByModelo(model)

        if(carList != null && carList.size <= 2){
            val service = APIServiceBuilder.createCarService()
            service.getCarsByModel(model).enqueue(object: Callback<List<CarResponse>> {
                override fun onResponse(
                    call: Call<List<CarResponse>>,
                    response: Response<List<CarResponse>>
                ) {
                    if (response.isSuccessful) {
                       /* Log.d("VIEWMODEL SEARCH Model",response.toString())
                        Log.d("1 VIEWMODEL SEARCH Model",cars.toString())
                        Log.d("1 VIEWMODEL SEARCH Model S",cars.size.toString())
                        cars  = mutableListOf<CarResponse>()
                        Log.d("2 VIEWMODEL SEARCH Model",cars.toString())
                        Log.d("2 VIEWMODEL SEARCH Model S",cars.size.toString())
                        cars.addAll(response.body()!!)
                        Log.d("3 VIEWMODEL SEARCH Model",cars.toString())
                        Log.d("3 VIEWMODEL SEARCH Model S",cars.size.toString())
                        getData(response.body()!!)*/
                        appDatabase.getIntance()?.carDao()?.insertAll(Car.getCarEntityFromCarResponse(response.body()!!))
                        carList = appDatabase.getIntance()?.carDao()?.getCarsByModelo(model)
                        if( !carList.isNullOrEmpty() ){
                            cars.addAll(carList!!)
                            getData(appDatabase.getIntance()?.carDao()?.getCarsByModelo(model))
                        }
                    }
                }
                override fun onFailure(call: Call<List<CarResponse>>, t: Throwable) {
                    //Not yet implemented
                }
            })
        }else{
            //cars  = mutableListOf<CarResponse>()
            cars.addAll(carList!!)
            getData(cars)
        }
    }

    private fun getAllCars() {
        getGasCars()
        getDieselCars()
        getElectricCars()
    }
    private fun getLogoImages() {
        val mDbHelper = DBHelper.getIntance()//(context,null)
        val logoList : List<LogoResponse> = mDbHelper.getAllLogos()
        if(logoList.size < marcasList.size) {
            val logoService = APIServiceBuilder.createLogoService()
            for (marca in marcasList) {
                if(marca.url.isNullOrEmpty()){
                    logoService.getLogoByName(marca.name).enqueue(object: Callback<List<LogoResponse>> {
                        override fun onResponse(call: Call<List<LogoResponse>>, response: Response<List<LogoResponse>>) {
                            if (response.isSuccessful) {
                                val logos = response.body()
                                if (!logos.isNullOrEmpty()) {
                                    marca.url = logos[0].imagenURL
                                    mDbHelper.addLogos(logos)
                                    //setupRecView()
                                    //showData(cars)
                                }
                            } else {
                                // Handle error
                            }
                        }

                        override fun onFailure(call: Call<List<LogoResponse>>, t: Throwable) {
                            // Handle failure
                        }
                    })
                }
            }
        } else{
            for(marca in marcasList){
                if(marca.url.isNullOrEmpty()){
                    if(logoList.find{it.nombre == marca.name } != null){
                        marca.url = logoList.find{it.nombre == marca.name}!!.imagenURL

                    }
                }
            }
            //showData(cars)
        }
    }
    private fun showData(carBunch: MutableList<Car>) {
        Log.d("showData: ", carBunch.size.toString())
        carList =  v.findViewById<RecyclerView>(R.id.carListView)

        carList.apply {
           layoutManager = LinearLayoutManager(context)
            adapter = CarAdapter(carBunch, context, false, fireBaseAuth.currentUser?.email.toString())
        }
        //CleanUp
        viewModel.buscar("", "")
    }

    // TODO: Remove unused fun
    /*private fun getFragment(){
        val service = APIServiceBuilder.createCarService()

        service.getCarsList().enqueue(object: Callback<List<CarResponse>> {
            override fun onResponse(
                call: Call<List<CarResponse>>,
                response: Response<List<CarResponse>>
            ) {
                if (response.isSuccessful) {
                    //showData(getLogoImages(response.body()!!))
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<List<CarResponse>>, t: Throwable) {
                //Not yet implemented
            }
        }

        )

    }
    private fun getMarcasUnicas(carList: List<CarResponse>): List<String> {
    val unicas = carList.map { it.marca.replace(" ","") }.distinct()
    return unicas.toList()
    }*/
    /*    private fun getCars() {
        /*if(viewModel.tipoBusqueda.toString()=="gas"){
            getGasCars()
        }
        if(viewModel.tipoBusqueda.toString()=="diesel"){
            getDieselCars()
        }
        if(viewModel.tipoBusqueda.toString()=="electricity"){
            getElectricCars()
        }*/
    }

    private fun getAllCars(){
        val service = APIServiceBuilder.createCarService()
        var cars = mutableListOf<CarResponse>()
        service.getCarsByFuel("gas").enqueue(object: Callback<List<CarResponse>> {
            override fun onResponse(
                call: Call<List<CarResponse>>,
                response: Response<List<CarResponse>>
            ) {
                if (response.isSuccessful) {
                    cars.addAll(response.body()!!)
                    getLogoImages(cars)
                } else {
                    // Handle error
                }
            }
            override fun onFailure(call: Call<List<CarResponse>>, t: Throwable) {
                //Not yet implemented
            }
        })
        service.getCarsByFuel("diesel").enqueue(object: Callback<List<CarResponse>> {
            override fun onResponse(
                call: Call<List<CarResponse>>,
                response: Response<List<CarResponse>>
            ) {
                if (response.isSuccessful) {
                    cars.addAll(response.body()!!)
                    getLogoImages(cars)
                } else {
                    // Handle error
                }
            }
            override fun onFailure(call: Call<List<CarResponse>>, t: Throwable) {
                //Not yet implemented
            }
        })
        service.getCarsByFuel("electricity").enqueue(object: Callback<List<CarResponse>> {
            override fun onResponse(
                call: Call<List<CarResponse>>,
                response: Response<List<CarResponse>>
            ) {
                if (response.isSuccessful) {
                    cars.addAll(response.body()!!)
                    getLogoImages(cars)
                } else {
                    // Handle error
                }
            }
            override fun onFailure(call: Call<List<CarResponse>>, t: Throwable) {
                //Not yet implemented
            }
        })
        Log.d("getAllCars: ", cars.toString())
        getLogoImages(cars)
    }
    */
    /*private fun getLogoImages(carList: MutableList<CarResponse>) {
        val logoService = APIServiceBuilder.createLogoService()
        val marcas = getMarcasUnicas(carList)
        for (marca in marcas) {
            logoService.getLogoByName(marca).enqueue(object: Callback<List<LogoResponse>> {
                override fun onResponse(call: Call<List<LogoResponse>>, response: Response<List<LogoResponse>>) {
                    if (response.isSuccessful) {
                        val logos = response.body()
                        if (!logos.isNullOrEmpty()) {
                            carList.forEach { car -> if(car.marca.equals(marca)) car.image=logos[0].imagenURL}
                            //car.image = logos[0].imagenURL
                        }
                    } else {
                        // Handle error
                    }
                }

                override fun onFailure(call: Call<List<LogoResponse>>, t: Throwable) {
                    // Handle failure
                }
            })
        }
        Log.d("getLogoImages: ", carList.toString())

        showData(carList.toList())
    }
    */
}