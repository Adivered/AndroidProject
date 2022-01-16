
/*
 private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Homepage.this);
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
            if (username == null)
                return;
            else {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String message = jsonObject.getString("Message");
                        String date = jsonObject.getString("Date");
                        String from = jsonObject.getString("From");
                        String visible = jsonObject.getString("Visible");
                        if (visible.equals("כולם")) {
                            String msg = date + " - " + from + "\n" + "NEW" +
                                    message + "\n" + "NEW";
                            setMessage(msg);
                        } else if (username.equals(visible)) {
                            String msg = "**אישי**" + "\n" +
                                    date + " - " + from + "\n" + "NEW" +
                                    message + "\n" + "NEW";
                            setMessage(msg);
                        } else {
                            Log.v("VISIBLE", "הודעה לא ניתנת לצפיה למשתמש שלך");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void setMessage(String msg) {
        Log.v("BACKGROUND RESPONSE", " CHECK ---->" + msg);
        Log.v("firstMSG", " Status ---->" + firstMsg);
            Log.v("setMessage", "first time, attempting to add" + msg);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            linearParams.setMargins(0,0,5,0);
            linearParams.gravity = Gravity.CENTER;
            String[] parts = msg.split("NEW");
            for( int i = 0; i < parts.length; i++){
                TextView messages = new TextView(Homepage.this);
                messages.setLayoutParams(linearParams);
                messages.setText(parts[i]);
                if(i == 0) {
                    messages.setTextAppearance(this, R.style.fontForDateNotif);
                }
                else if (i == 1){
                    messages.setTextAppearance(this, R.style.fontForMessageNotif);
                }
                messages.setId(R.id.messageTxt);
                messages.setGravity(Gravity.CENTER);
                msgLinearLayout.addView(messages);
                firstMsg = false;
            }

        }*/