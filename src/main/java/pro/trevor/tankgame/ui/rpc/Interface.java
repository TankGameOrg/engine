package pro.trevor.tankgame.ui.rpc;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Interface {

    protected RpcHandler handler;
    protected Map<String, Method> methodMap;

    public Interface(RpcHandler handler) {
        this.handler = handler;
        methodMap = new HashMap<>();

        for(Method method : handler.getClass().getMethods()) {
            RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
            if(rpcMethod == null) continue;

            if(!JSONObject.class.isAssignableFrom(method.getReturnType())) {
                throw new Error("Rpc method " + method.getName() + " does not return a JSONObject");
            }

            if(method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(JSONObject.class)) {
                throw new Error("Rpc method " + method.getName() + " must accept exactly 1 JSONObject parameter");
            }

            methodMap.put(method.getName(), method);
        }
    }

    public RpcHandler manualHandler() {
        return this.handler;
    }

    public JSONObject handleJson(JSONObject json) {
        if (!json.has("method")) {
            return response(new Error("Request does not have the required field method"));
        }

        String methodName = json.getString("method");
        Method method = methodMap.get(methodName);
        if(method == null) {
            return response(new Error("Method " + methodName + " not found"));
        }

        try {
            return (JSONObject) method.invoke(handler, json);
        } catch(Throwable exception) {
            return response(exception);
        }

    }

    protected JSONObject encodeThrowable(Throwable error) {
        JSONObject output = new JSONObject();
        output.put("message", error.getMessage());
        output.put("stack", error.getStackTrace());
        if(error.getCause() != null) {
            output.put("cause", encodeThrowable(error.getCause()));
        }
        return output;
    }

    protected JSONObject response(Throwable error) {
        JSONObject output = new JSONObject();
        output.put("error", encodeThrowable(error));
        return output;
    }
}
