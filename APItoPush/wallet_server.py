from gcm import GCM
from flask import Flask, request, abort, jsonify
import threading
import atexit
import requests
import json


capital_api = "326b92975d191c335b19f9c256850259"
purchases_url = "http://api.reimaginebanking.com/accounts/{}/purchases?key=326b92975d191c335b19f9c256850259"
reg_ids = []
id_mapping = {}
lock = threading.Lock()
thread = threading.Thread()
POOL_TIME = 1
gcm = GCM("AIzaSyC0uNzPj4uVSdg0G15a_Mg5ddknOzjpFFE")

def create_app():
    app = Flask(__name__)

    def interrupt():
        global thread
        thread.cancel()

    def doStuff():
        global commonDataStruct
        global thread
        with lock:
        # Do your stuff with commonDataStruct Here
             for user in id_mapping:
                 purchases = get_purchases(id_mapping[user]["bankid"])
                 ## find if there are new purchases
                 old_purchases = id_mapping[user]["purchases"]
                 
                 if cmp(purchases, old_purchases):
                     print "new purchase detected"
                     id_mapping[user]["purchases"] = purchases
                     alert_device(id_mapping[user]["regid"])
                 
                 
        # Set the next thread to happen
        thread = threading.Timer(POOL_TIME, doStuff, ())
        thread.start()   

    def doStuffStart():
        # Do initialisation stuff here
        global thread
        # Create your thread
        thread = threading.Timer(POOL_TIME, doStuff, ())
        thread.start()

    # Initiate
    doStuffStart()
    # When you kill Flask (SIGTERM), clear the trigger for the next thread
    atexit.register(interrupt)
    
        
    return app

app = create_app()  

def get_purchases(bankid):
    url  = purchases_url.format(bankid,capital_api)
    print url
    response = requests.get(
        url
    )
    return response.json()
    
def alert_device(regid):
    response = gcm.json_request(registration_ids=[regid], data={"newpurchase" : "true"})
    print str(response)

@app.route('/')
def print_reg():
    html = "<h3>Registred Ids</h3>"
    for i in reg_ids:            
        regid = i.id
        html += "<p> " + str(regid) + "</p>"
    return html
    
@app.route('/reg', methods=['POST'])
def register():
    regid = request.form["regid"]
    bankid = request.form["bankid"]
    if regid in id_mapping:
        #error
        abort(403)
        
    purchases = get_purchases(bankid) 
    id_mapping[regid] = {"regid" : regid, "bankid" : bankid, "purchases" : purchases}

    print reg_ids
    print id_mapping
    return jsonify(**{"status" : 200, "regid" : regid, "bankid" : bankid})
    
app.run(debug=True, host='0.0.0.0')