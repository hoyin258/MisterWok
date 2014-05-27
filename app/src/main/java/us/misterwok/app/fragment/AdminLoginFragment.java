package us.misterwok.app.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import us.misterwok.app.Constants;
import us.misterwok.app.R;
import us.misterwok.app.api.APIEngine;

/**
 * Created by hoyin on 25/5/14.
 * Yintro.com
 */
public class AdminLoginFragment extends BaseFragment {

    private Button buttonLogin;
    private EditText editTextEmail;
    private EditText editTextPassword;

    public static AdminLoginFragment newInstance() {
        return new AdminLoginFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_login, container, false);

        buttonLogin = (Button) view.findViewById(R.id.button_login);
        editTextEmail = (EditText) view.findViewById(R.id.edit_text_admin_email);
        editTextPassword = (EditText) view.findViewById(R.id.edit_text_admin_password);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                        getActivity().getPackageName(), Activity.MODE_PRIVATE);

                final String email = editTextEmail.getText().toString();
                final String password = editTextPassword.getText().toString();
                String gcmId = sharedPreferences.getString(Constants.PREFERENCE_GCM_REGISTRATION, "");

                RequestParams requestParams = new RequestParams();
                requestParams.put("email", email);
                requestParams.put("password", password);
                requestParams.put("gcm_id", gcmId);

                getActivity().setProgressBarIndeterminateVisibility(true);
                APIEngine.registerAdmin(requestParams, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseBody) {

                                getActivity().setProgressBarIndeterminateVisibility(false);
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                        getActivity().getPackageName(), Activity.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sharedPreferences.edit();
                                edit.putString(Constants.PREFERENCE_ADMIN_EMAIL, email);
                                edit.putString(Constants.PREFERENCE_ADMIN_PASSWORD, password);
                                edit.commit();
                                NavUtils.navigateUpFromSameTask(getActivity());
                            }
                        }
                );
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
