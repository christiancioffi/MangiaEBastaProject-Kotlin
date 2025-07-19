package com.example.mangiabastakotlin.ViewModel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiabastakotlin.Exceptions.IncompleteProfileException
import com.example.mangiabastakotlin.Exceptions.LocationNotAvailableException
import com.example.mangiabastakotlin.Exceptions.NetworkErrorException
import com.example.mangiabastakotlin.Exceptions.NotFoundException
import com.example.mangiabastakotlin.Model.AppRequest
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.LocationController
import com.example.mangiabastakotlin.Model.ImageState
import com.example.mangiabastakotlin.Model.LastOrderState
import com.example.mangiabastakotlin.Model.ProfileState
import com.example.mangiabastakotlin.Model.Repositories.MenuRepository
import com.example.mangiabastakotlin.Model.Repositories.UserRepository
import com.example.mangiabastakotlin.Model.UserDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class ProfileViewModel(database: AppDatabase, private val locationController: LocationController): ViewModel() {
    private val TAG=ProfileViewModel::class.simpleName;
    private val userRepository= UserRepository();
    private val menuRepository= MenuRepository(database);

    private val _profileState= MutableStateFlow<ProfileState>(ProfileState());
    val profileState: StateFlow<ProfileState> = _profileState;
    private var userDetails: UserDetails = UserDetails();
    //private val _userDetailsState= UserDetailsMutableState(userDetails);
    //val userDetailsState= UserDetailsState(_userDetailsState);
    private val _lastOrderState= MutableStateFlow<LastOrderState>(LastOrderState());
    val lastOrderState: StateFlow<LastOrderState> = _lastOrderState;
    private val _lastOrderImageState= MutableStateFlow<ImageState>(ImageState());
    val lastOrderImageState: StateFlow<ImageState> = _lastOrderImageState;
    private val _appRequest=MutableStateFlow<AppRequest>(AppRequest());
    val appRequest:StateFlow<AppRequest> = _appRequest;

    init{
        Log.d(TAG,"ViewModel created!");
        loadUserDetails();
    }

    fun retry(){
        _profileState.value=ProfileState();
        loadUserDetails();
    }

    private fun loadUserDetails(){
        viewModelScope.launch{
            try{
                userDetails=userRepository.getUser();
                Log.d(TAG, "$userDetails");
                //_userDetailsState.updateAllDetails(userDetails);
               _profileState.value= ProfileState(loadingState="Loaded");
                loadLastOrder();
            }catch(nee: NetworkErrorException){
                Log.d(TAG, nee.message)
                _profileState.value= ProfileState(loadingState = "Error", errorMessage=nee.message);
            }catch(nfe: NotFoundException){
                Log.d(TAG, nfe.message);
                _profileState.value= ProfileState(loadingState = "Error", errorMessage="User not found");
            }catch (ce: CancellationException){
                Log.d(TAG, "Job cancelled");
                throw CancellationException();
            }catch(e:Exception){
                Log.d(TAG, "Error while retrieving profile details",e);
                _profileState.value= ProfileState(loadingState="Error", errorMessage="Something went wrong");
            }
        }
    }

    private suspend fun loadLastOrder() {
        try {
            val lastOrder = menuRepository.getOrderDetails(userDetails.lastOid);
            Log.d(TAG, "$lastOrder");
            val location = locationController.getLocation();
            val lastOrderedMenu = menuRepository.getMenuDetails(lastOrder.mid, location);
            _lastOrderState.value =
                LastOrderState(
                    loadingState = "Loaded",
                    lastOrder = lastOrder,
                    lastOrderedMenu = lastOrderedMenu
                )
            loadLastOrderImage();
        } catch (nee: NetworkErrorException) {
            Log.d(TAG, nee.message)
            _lastOrderState.value = LastOrderState(loadingState = "Error", errorMessage = "Last order not available")
        }catch(nfe: NotFoundException){
            Log.d(TAG, nfe.message);
            _lastOrderState.value = LastOrderState(loadingState = "Error", errorMessage = "Last order not found")
        }catch(le: LocationNotAvailableException){
            Log.d(TAG, le.message)
            _lastOrderState.value= LastOrderState(loadingState="Error", errorMessage="Last order not available")
        }catch (ce: CancellationException){
            Log.d(TAG, "Job cancelled");
            throw CancellationException();
        }catch(e:Exception){
            Log.d(TAG, "Error while retrieving last order details",e)
            _lastOrderState.value= LastOrderState(loadingState="Error", errorMessage="Last order not available")
        }
    }

    private suspend fun loadLastOrderImage(){
        try{
            val base64Image=menuRepository.getMenuImage(mid=_lastOrderState.value.lastOrderedMenu.mid, imageVersion=_lastOrderState.value.lastOrderedMenu.imageVersion);
            val lastOrderedMenuImage=getBitmapFromBase64(base64Image);
            _lastOrderImageState.value= ImageState(loadingState="Loaded",image=lastOrderedMenuImage)
        }catch (ce: CancellationException){
            Log.d(TAG, "Job cancelled");
            throw CancellationException();
        }catch(e: Exception){
            Log.d(TAG,e.message ?: "Error");
            _lastOrderImageState.value= ImageState(loadingState="Error");
        }
    }

    private fun getBitmapFromBase64(base64Image: String):ImageBitmap{
        //delay(5000);
        val byteArray = Base64.decode(base64Image);
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return bitmap.asImageBitmap();
    }



    fun setFirstName(newValue: String){
        Log.d(TAG, "Updating FirstName from ${userDetails.firstName} to $newValue");
        userDetails.firstName=newValue;
    }

    fun getFirstName():String{
        return userDetails.firstName;
    }

    fun getFirstNamePlaceholder():String{
        return "Mario";
    }

    fun setLastName(newValue: String){
        Log.d(TAG, "Updating LastName from ${userDetails.lastName} to $newValue");
        userDetails.lastName=newValue;
    }

    fun getLastName():String{
        return userDetails.lastName;
    }

    fun getLastNamePlaceholder():String{
        return "Rossi";
    }

    fun setCardFullName(newValue: String){
        Log.d(TAG, "Updating CardFullName from ${userDetails.cardFullName} to $newValue");
        userDetails.cardFullName=newValue;
    }

    fun getCardFullName():String{
        return userDetails.cardFullName;
    }

    fun getCardFullNamePlaceholder():String{
        return "Mario Rossi";
    }

    fun setCardNumber(newValue: String){
        Log.d(TAG, "Updating CardNumber from ${userDetails.cardNumber} to $newValue");
        userDetails.cardNumber=newValue;
    }

    fun getCardNumber():String{
        return userDetails.cardNumber;
    }

    fun getCardNumberPlaceholder():String{
        return "1234567812345678";
    }

    fun setCardExpireMonth(newValue: String){
        val months = arrayOf(
            "january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december"
        );
        var monthIndex=months.indexOf(newValue.lowercase());      //-1 automaticamente se newValue non è in months
        if(monthIndex!=-1){
            monthIndex+=1;
        }
        Log.d(TAG, "Updating CardExpireMonth from ${userDetails.cardExpireMonth} to $monthIndex");
        userDetails.cardExpireMonth=monthIndex;
    }

    fun getCardExpireMonth():String{
        val months = arrayOf(
            "january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december"
        );
        val monthIndex=userDetails.cardExpireMonth;
        if(monthIndex==-1){
            return "";
        }else{
            return months[monthIndex-1].replaceFirstChar { it.uppercase() };
        }
    }

    fun getCardExpireMonthPlaceholder():String{
        val months = arrayOf(
            "january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december"
        );
        val monthIndex=LocalDate.now().monthValue;      //(1-based)
        return months[monthIndex-1].replaceFirstChar { it.uppercase() };
    }

    fun setCardExpireYear(newValue: String){
        val newValueInt=newValue.toIntOrNull() ?: -1;
        Log.d(TAG, "Updating CardExpireYear from ${userDetails.cardExpireYear} to $newValueInt");
        userDetails.cardExpireYear=newValueInt;
    }

    fun getCardExpireYear():String{
       if(userDetails.cardExpireYear==-1){
           return ""
       }else{
           return "${userDetails.cardExpireYear}";
       }
    }

    fun getCardExpireYearPlaceholder():String{
        return "${LocalDate.now().year}";
    }

    fun setCardCVV(newValue: String){
        Log.d(TAG, "Updating CardCVV from ${userDetails.cardCVV} to $newValue");
        userDetails.cardCVV=newValue;
    }

    fun getCardCVV():String{
        return userDetails.cardCVV;
    }

    fun getCardCVVPlaceholder():String{
        return "123";
    }

    fun sendUserDetails(){
        viewModelScope.launch{
            try{
                _appRequest.value=AppRequest(requestState="Waiting");
                Log.d(TAG, "$userDetails");
                userDetails.isComplete();
                userRepository.sendDetails(userDetails);
                _appRequest.value=AppRequest(
                    requestState="Received",
                    title="Success",
                    message="Your profile has been updated successfully!",
                )
            }catch(nee: NetworkErrorException){
                Log.d(TAG,nee.message)
                _appRequest.value=AppRequest(
                    requestState="Received",
                    title="Error",
                    message=nee.message,
                )
            }catch(ipe: IncompleteProfileException){
                Log.d(TAG,"${ipe.message}. Wrong field is ${ipe.wrongField}");
                _appRequest.value=AppRequest(
                    requestState="Received",
                    title="Error",
                    message="Your profile is not complete. Check \"${ipe.wrongField}\" field.",
                )
            } catch (ce: CancellationException){
                Log.d(TAG, "Job cancelled");
                throw CancellationException();
            } catch(e: Exception){
                Log.d(TAG,"${e.message}");
                _appRequest.value=AppRequest(
                    requestState="Received",
                    title="Error",
                    message="Something went wrong",
                )
            }
        }
    }

    fun deleteAlert(){
        _appRequest.value=AppRequest();
    }

    //For debugging
    override fun onCleared() {
        super.onCleared();
        Log.d(TAG, "ViewModel destroyed.")
    }

}