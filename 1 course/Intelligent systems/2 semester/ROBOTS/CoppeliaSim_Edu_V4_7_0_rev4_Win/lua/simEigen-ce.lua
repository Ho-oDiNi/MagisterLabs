local codeEditorInfos=[[
grid s, grid u, grid v, grid x = simEigen.svd(grid m, bool computeThinU=true, bool computeThinV=true, grid b=nil)
grid m, grid x = simEigen.pinv(grid m, grid b=nil, float damping=0)
]]

registerCodeEditorInfos("simEigen",codeEditorInfos)
