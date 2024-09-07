package pro.trevor.tankgame.ui;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Cli {
    IRpcHandler handler;
    Map<String, Method> methodMap;

    public Cli(IRpcHandler handler) {
        this.handler = handler;
        methodMap = new HashMap<>();

        for(Method method : handler.getClass().getMethods()) {
            RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
            if(rpcMethod == null) continue;

            if(!method.getReturnType().equals(JSONObject.class)) {
                throw new Error("Rpc method " + method.getName() + " does not return a JSONObject");
            }

            if(method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(JSONObject.class)) {
                throw new Error("Rpc method " + method.getName() + " must accept exactly 1 JSONObject parameter");
            }

            methodMap.put(method.getName(), method);
        }
    }

    public void startRepl() {
        startRepl(System.in, System.out);
    }

    public void startRepl(InputStream input, PrintStream output) {
        Scanner scanner = new Scanner(input);
        JSONObject json;
        while(handler.canProcessRequests()) {
            json = getJsonObject(scanner);
            if (!json.has("method")) {
                output.println(response(new Error("Request does not have the required method field")));
                continue;
            }

            String methodName = json.getString("method");
            Method method = methodMap.get(methodName);
            if(method == null) {
                output.println(response(new Error("The method " + methodName + " does not exist")));
                continue;
            }

            try {
                output.println(method.invoke(handler, json));
            }
            catch(IllegalAccessException | InvocationTargetException ex) {
                output.println(response(ex));
            }
            catch(Throwable ex) {
                output.println(response(ex));
            }
        }
    }

    private JSONObject getJsonObject(Scanner input) {
        Pattern oldPattern = input.delimiter();
        StringBuilder sb = new StringBuilder();
        input.useDelimiter("");
        int count = 0;
        boolean seeking = true;
        boolean inQuote = false;
        do {
            char current = input.next().charAt(0);
            sb.append(current);
            if (current == '{' && !inQuote) {
                count = count + 1;
                seeking = false;
            } else if (current == '}' && !inQuote) {
                count = count - 1;
            } else if (current == '\"')  {
                inQuote = !inQuote;
            }

        } while (count > 0 || seeking);

        input.useDelimiter(oldPattern);

        try {
            return new JSONObject(sb.toString());
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private JSONObject encodeThrowable(Throwable error) {
        JSONObject output = new JSONObject();
        output.put("message", error.getMessage());
        output.put("stack", error.getStackTrace());
        if(error.getCause() != null) {
            output.put("cause", encodeThrowable(error.getCause()));
        }
        return output;
    }

    private JSONObject response(Throwable error) {
        JSONObject output = new JSONObject();
        output.put("error", encodeThrowable(error));
        return output;
    }
}
