local codeEditorInfos=[[
string serverHandle = simWS.start(int listenPort)
simWS.setOpenHandler(string serverHandle, string callbackFn)
simWS.setFailHandler(string serverHandle, string callbackFn)
simWS.setCloseHandler(string serverHandle, string callbackFn)
simWS.setMessageHandler(string serverHandle, string callbackFn)
simWS.setHTTPHandler(string serverHandle, string callbackFn)
simWS.send(string serverHandle, string connectionHandle, buffer data, int opcode=simws_opcode_text)
simWS.stop(string serverHandle)
simWS.opcode.continuation
simWS.opcode.text
simWS.opcode.binary
]]

registerCodeEditorInfos("simWS",codeEditorInfos)
