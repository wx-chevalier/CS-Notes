# Web Worker

You cannot use Local Storage in service workers. It was decided that service workers should not have access to any synchronous APIs. You can use IndexedDB instead, or communicate with the controlled page using postMessage().

By default, cookies are not included with fetch requests, but you can include them as follows: fetch(url, {credentials: 'include'}).
