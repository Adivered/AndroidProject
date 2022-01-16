    /*@Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("לסגור?").setPositiveButton("אישור",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }*/

    /*    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("טוען..");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                 if (result == null || result.toString().isEmpty()) {
                Log.v("Result", "null");
                 } else {
                try {
                    Log.v("ATTMPT", result.toString());
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        backgroundUsername = jsonObject.getString("username");
                        //backgroundRank = jsonObject.getString("loggedin");
                        backgroundDate = jsonObject.getString("date");
                    }
                    if (session.isUserLoggedIn()) {
                        Log.d("Session status: ", "> " + session.returnUsername());
                        Log.d("API status: ", "> " + backgroundUsername);
                        String myFormat = "yyyy-MM-dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        if (session.returnUsername().equals(backgroundUsername) && backgroundDate.equals(sdf.format(myCalendar.getTime()))) {
                            Log.d("EQUAL: ", "> REDIRECTS");
                            session.isUserLoggedInKnisa();
                            Intent intent = new Intent(getApplicationContext(), Homepage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            pd.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        }*/



    /*        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="@string/google_maps_key" />*/