package burp;

import java.awt.Component;
import java.io.PrintWriter;
import java.util.List;

public class BurpExtender implements IBurpExtender, IHttpListener, ITab
{
    private IExtensionHelpers helpers;
    private MatchReplaceConfigurationPanel configuration;
    private IBurpExtenderCallbacks callbacks;
    private PrintWriter stdout;

    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        stdout = new PrintWriter(callbacks.getStdout(), true);
        configuration = new MatchReplaceConfigurationPanel();
        callbacks.customizeUiComponent(configuration);
        callbacks.addSuiteTab(this);
        callbacks.registerHttpListener(this);
    }



    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse request)
    {
        if(messageIsRequest)
        {
            String i_request = helpers.bytesToString(request.getRequest());
            List<MatchReplace> data = configuration.getData();
            System.out.println("Length " + configuration.getdataCount());
            //stdout.println("======= Start===========");
            for(int index = 0; index < configuration.getdataCount(); index++){
                MatchReplace element = data.get(index);
                String to_match = element.getMatch();
                String to_replace = element.getReplace();
                System.out.println(to_match);
                System.out.println(to_replace);
                int count = element.getCount();
                if(to_match.contains("#increment#")){
                    stdout.println("======= increment ===========");
                    i_request = i_request.replaceAll(to_match, to_replace + Integer.toString(count));
                    configuration.setData(index, ++count);
                }else if(to_match.contains("#decrement#")){
                    stdout.println("======= decrement ===========");
                    i_request = i_request.replaceAll(to_match, to_replace + Integer.toString(count));
                    configuration.setData(index, --count);
                }else {
                    stdout.println("======= replace ===========");
                    i_request = i_request.replaceAll(to_match, to_replace);
                }
            }
            request.setRequest(helpers.stringToBytes(i_request));
            //we do the repeat the rest below so that we can calculate the correct content length
            IRequestInfo reqInfo = helpers.analyzeRequest(request);
            List headers = reqInfo.getHeaders();
            int offset = reqInfo.getBodyOffset();
            byte[] _request = request.getRequest();
            String req_string = new String(_request);
            String body_json = req_string.substring(offset);
            byte[] updateMessage = helpers.buildHttpMessage(headers, body_json.getBytes());
            request.setRequest(updateMessage);
            //stdout.println("======= END ===========");

        }

    }
    @Override
    public String getTabCaption() {
        return "Match and replace";
    }

    @Override
    public Component getUiComponent() {
        return configuration;
    }
}
