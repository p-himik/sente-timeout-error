## Usage

Run the server:
    
    $ clj -M:run-server

Wait till it says "The server is running".
    
Run the UI:

    $ clj -M:run-ui
    
Note what URL the latter command outputs after "shadow-cljs - HTTP server available at" and open it in your browser.

When you open the browser's DevTools, you should see `Response :chsk/timeout` in its Console output.
