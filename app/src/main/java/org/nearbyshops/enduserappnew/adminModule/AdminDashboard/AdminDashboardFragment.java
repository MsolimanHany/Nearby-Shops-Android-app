package org.nearbyshops.enduserappnew.adminModule.AdminDashboard;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import org.nearbyshops.enduserappnew.API.ServiceConfigurationService;
import org.nearbyshops.enduserappnew.Model.ModelServiceConfig.ServiceConfigurationLocal;
import org.nearbyshops.enduserappnew.Preferences.PrefLogin;
import org.nearbyshops.enduserappnew.Preferences.PrefServiceConfig;
import org.nearbyshops.enduserappnew.EditServiceConfig.EditConfiguration;
import org.nearbyshops.enduserappnew.UsersList.UsersList;
import org.nearbyshops.enduserappnew.adminModule.ItemsDatabaseForAdmin.ItemsDatabaseAdmin;
import org.nearbyshops.enduserappnew.adminModule.ShopsList.ShopsDatabase;
import org.nearbyshops.enduserappnew.DaggerComponentBuilder;
import org.nearbyshops.enduserappnew.EditProfile.EditProfile;
import org.nearbyshops.enduserappnew.EditProfile.FragmentEditProfile;
import org.nearbyshops.enduserappnew.Interfaces.NotifyAboutLogin;
import org.nearbyshops.enduserappnew.OrderHistory.OrderHistory;
import org.nearbyshops.enduserappnew.R;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdminDashboardFragment extends Fragment {





    @Inject
    ServiceConfigurationService configurationService;



    public AdminDashboardFragment() {
        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        ButterKnife.bind(this,rootView);


        return rootView;
    }








    @OnClick(R.id.items_database)
    void optionItemCatApprovals()
    {
        startActivity(new Intent(getActivity(), ItemsDatabaseAdmin.class));
    }



    @OnClick(R.id.service_config)
    void serviceCOnfigClick()
    {
//        startActivity(new Intent(getActivity(), EditServiceConfiguration.class));


        getServiceConfig(true);
    }









    private void getServiceConfig(final boolean launchEditConfig)
    {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("loading");
        pd.show();

        Call<ServiceConfigurationLocal> call = configurationService.getServiceConfiguration(null,null);



        call.enqueue(new Callback<ServiceConfigurationLocal>() {
            @Override
            public void onResponse(Call<ServiceConfigurationLocal> call, Response<ServiceConfigurationLocal> response) {

                if(response.code()==200 && response.body()!=null)
                {

                    PrefServiceConfig.saveServiceConfigLocal(
                            response.body(),getActivity()
                    );


                    if(launchEditConfig)
                    {
                        startActivity(new Intent(getActivity(), EditConfiguration.class));
                    }
                }
                else
                {
                    System.out.println("Failed to get config " + response.code());
                }

                pd.dismiss();
            }

            @Override
            public void onFailure(Call<ServiceConfigurationLocal> call, Throwable t) {


                pd.dismiss();

                System.out.println("Check your network !");
            }
        });

    }










    @OnClick(R.id.item_specifications)
    void itemSpecNameClick()
    {
//        Intent intent = new Intent(getActivity(), ItemSpecName.class);
//        startActivity(intent);
    }







    void showToastMessage(String message)
    {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }






    @OnClick(R.id.shop_approvals)
    void optionAdminClick(View view)
    {
        Intent intent = new Intent(getActivity(), ShopsDatabase.class);
        startActivity(intent);
    }





    @OnClick(R.id.staff_accounts)
    void optionStaffClick(View view)
    {
        startActivity(new Intent(getActivity(), UsersList.class));
    }





    @OnClick(R.id.edit_profile)
    void editProfileClick()
    {
        Intent intent = new Intent(getActivity(), EditProfile.class);
        intent.putExtra(FragmentEditProfile.EDIT_MODE_INTENT_KEY, FragmentEditProfile.MODE_UPDATE);
        startActivity(intent);
    }




    @OnClick(R.id.orders_database)
    void ordersClick()
    {
        Intent intent = new Intent(getActivity(), OrderHistory.class);
        startActivity(intent);
    }






    @OnClick(R.id.logout)
    void logoutClick()
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle("Confirm Logout !")
                .setMessage("Do you want to log out !")
                .setPositiveButton("Yes",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        logout();

                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showToastMessage("Cancelled !");
                    }
                })
                .show();
    }









    void logout()
    {
        // log out
        PrefLogin.saveUserProfile(null,getActivity());
        PrefLogin.saveCredentials(getActivity(),null,null);

        // stop location update service

        if(getActivity() instanceof NotifyAboutLogin)
        {
            ((NotifyAboutLogin) getActivity()).loginSuccess();
        }
    }


}