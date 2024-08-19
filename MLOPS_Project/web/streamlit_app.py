import streamlit as st
import pandas as pd
import numpy as np
import tensorflow as tf
import joblib

# Load the trained TensorFlow model and preprocessor
model = tf.keras.models.load_model('../web/rf_model.h5')
preprocessor = joblib.load('preprocessor.joblib')

# Define the main function for the Streamlit app
def main():
    st.title("House Estim8")

    # Create a form for user inputs
    with st.form("prediction_form"):
        property_type = st.selectbox("Type", ["Apartment", "House"])
        room_num = st.number_input("Number of Rooms", min_value=1.5, step=0.5)
        # floor = st.text_input("Floor")  # Keep as text input
        area_m2 = st.number_input("Area (m2)", min_value=50, step=1)
        # floors_num = st.number_input("Number of Floors", min_value=1, step=1)
        year_built = st.number_input("Year Built", min_value=1900, max_value=2023, step=1)
        # last_refurbishment = st.number_input("Last Refurbishment Year", max_value=2023, step=1)
        zip_code = st.text_input("Zip Code")
        city = st.text_input("City")
        canton = st.text_input("Canton")

        # Submit button
        submitted = st.form_submit_button("Predict")

        # Form for prediction
        if submitted:
            target_property = {
                'type': [property_type],
                'room_num': [room_num],
                'floor': [0],
                'area_m2': [area_m2],
                'floors_num': [1],
                'year_built': [year_built],
                'last_refurbishment': [0],
                'zip_code': [zip_code],
                'city': [city],
                'canton': [canton],
            }

            input_data = pd.DataFrame(target_property)
            input_data_processed = preprocessor.transform(input_data)

            # Convert to dense matrix if needed
            if hasattr(input_data_processed, 'toarray'):
                input_data_processed = input_data_processed.toarray()

            prediction = model.predict(input_data_processed)
            st.write(f"Predicted Price: {round(prediction[0][0])} CHF")

if __name__ == "__main__":
    main()
