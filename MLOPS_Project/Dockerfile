# Use the official lightweight Python image.
# https://hub.docker.com/_/python
FROM python:3.9-slim

# Set the working directory to /web
WORKDIR /web

# Copy the current directory contents into the container at /app
COPY web/streamlit_app.py web/requirements.txt web/preprocessor.joblib web/rf_model.h5 ./


# Install any needed packages specified in requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# Make port 8501 available to the world outside this container
EXPOSE 8501

# Print if the container is running
RUN echo "Container is running"

# Print if the files are copied
RUN ls -l

# Run streamlit app when the container launches
CMD ["streamlit", "run", "streamlit_app.py"]
