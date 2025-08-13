# LunarstarConnector

> **WARNING:** This mod is intended for use with **LunarstarServer only** and is **not compatible** with other servers.

Connect to the Lunarstar Server faster and more securely.

### Why?

For connection optimization and enhanced security.

### Is it safe?

Yes. This mod is open source, allowing you to build it from source code yourself.

### What is Cloudflare?

Please see [this Wikipedia article](https://en.wikipedia.org/wiki/Cloudflare) for more information.

### Is Cloudflare Tunnel safe?

Yes. Cloudflare Tunnel establishes a secure outbound connection from your device to Cloudflare’s global network. It does **not** expose your real IP address and routes all traffic through Cloudflare’s infrastructure, thereby protecting your privacy and security.

### What will it do?

- Establish a Cloudflare Tunnel that connects your device outbound to Cloudflare’s global network  
- Route all Minecraft traffic through this secure tunnel to the Lunarstar server  
- Hide your real IP address from the server and the public internet  
- Ensure a stable and optimized connection to the server  
- Enforce any server-required resource pack policies

### What is cloudflared-xxx-xxx (or cloudflared-xxx-xxx.exe)?

This is **cloudflared**, the Cloudflare tunnel daemon.  
Please **do not close** it while you are playing.  
This executable will automatically stop when the game closes.  
If you see it still running after exiting, please manually close it if needed.

### What is lunarstar.lc.axurl.cc?

This is a custom domain used for the Cloudflare Tunnel connection. It resolves to the IP address `127.127.127.127`, which is a loopback address reserved for local machine communication only. This ensures that all traffic through the tunnel stays strictly within your device, enhancing security and privacy.

### What is 127.127.127.127?

`127.127.127.127` is part of the loopback IP address range (`127.0.0.0/8`), which means it refers only to the local computer, ensuring that the tunnel listener is accessible only from your device and cannot be reached from the public internet or any external network. This setup guarantees that your connection is secure and isolated.

### Why don’t you just use the IP address rather than the domain?

Using the domain name simplifies server management and avoids the need to remember multiple IP addresses. Players only need to connect to the domain, and all other configurations are handled behind the scenes.

### How to use?

1. Install the mod.  
2. Add or edit the server entry and click the "連線至星月紀" button at the bottom left.  
3. The mod will replace the server address with the tunnel server address.  
4. Save and connect.  
5. Enjoy your game!

The mod automatically manages the Cloudflare Tunnel for you, providing a faster and safer connection.

### Why can't I disable the resource pack?

The resource pack is required by server policy and must be enabled to play on the server.
