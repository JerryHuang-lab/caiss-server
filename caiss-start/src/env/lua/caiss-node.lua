-- load global route cache into current request scope
-- by default vars are not shared between requests

  -- try cached route first
  local function array_to_hash(t)
   local result = {}
    for i = 1, #t, 2 do
        result[t[i]] = t[i + 1]
    end
    return result
  end
  
  local function split(s, p)
    local rt= {}
    string.gsub(s, '[^'..p..']+', function(w) table.insert(rt, w) end )
    return rt
  end
  
  
  local function filterMasterNode(map)
    local result = {}
	if map ~= nil then
		for k, v in pairs(map) do
			table.insert(result,v)
		end
	end
	return result
  end
  
  
  local redis  = require "redis"
  local red = redis:new()
  local ok,err = red.connect(red, "172.17.0.1", 6379)
	if ok then
		local route  = red:hgetall('register_master_node')
		
		if next(route) ~= nil then
		  local map = array_to_hash(route)
		  local result = filterMasterNode(map)
		  if(next(result) ~= nil) then
			local i = math.random(1,#result)
			route = result[i]
		  end
		else
		  ngx.say("route is empty")
		  
		end
	end 
  

  -- fallback to redis for lookups
  if route ~= nil then
	ngx.var.caissNode = route
  else
	ngx.exit(ngx.HTTP_NOT_FOUND)
  end
  
  
  