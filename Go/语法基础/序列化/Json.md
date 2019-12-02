# Go 中的序列化

# 内建 Json 编解码器

```ts
type Person struct {
    Name   string
    Age    int
    Emails []string
    Extra  map[string]string
}

// This input can come from anywhere, but typically comes from
// something like decoding JSON where we're not quite sure of the
// struct initially.
input := map[string]interface{}{
    "name":   "Mitchell",
    "age":    91,
    "emails": []string{"one", "two", "three"},
    "extra": map[string]string{
        "twitter": "mitchellh",
    },
}

var result Person
err := Decode(input, &result)
if err != nil {
    panic(err)
}

fmt.Printf("%#v", result)
```

# jsonparser

```go
import "github.com/buger/jsonparser"

// ...

data := []byte(`{
  "person": {
    "name": {
      "first": "Leonid",
      "last": "Bugaev",
      "fullName": "Leonid Bugaev"
    },
    "github": {
      "handle": "buger",
      "followers": 109
    },
    "avatars": [
      { "url": "https://avatars1.githubusercontent.com/u/14009?v=3&s=460", "type": "thumbnail" }
    ]
  },
  "company": {
    "name": "Acme"
  }
}`)

// You can specify key path by providing arguments to Get function
jsonparser.Get(data, "person", "name", "fullName")

// There is `GetInt` and `GetBoolean` helpers if you exactly know key data type
jsonparser.GetInt(data, "person", "github", "followers")

// When you try to get object, it will return you []byte slice pointer to data containing it
// In `company` it will be `{"name": "Acme"}`
jsonparser.Get(data, "company")

// If the key doesn't exist it will throw an error
var size int64
if value, err := jsonparser.GetInt(data, "company", "size"); err == nil {
  size = value
}

// You can use `ArrayEach` helper to iterate items [item1, item2 .... itemN]
jsonparser.ArrayEach(data, func(value []byte, dataType jsonparser.ValueType, offset int, err error) {
	fmt.Println(jsonparser.Get(value, "url"))
}, "person", "avatars")

// Or use can access fields by index!
jsonparser.GetInt("person", "avatars", "[0]", "url")

// You can use `ObjectEach` helper to iterate objects { "key1":object1, "key2":object2, .... "keyN":objectN }
jsonparser.ObjectEach(data, func(key []byte, value []byte, dataType jsonparser.ValueType, offset int) error {
        fmt.Printf("Key: '%s'\n Value: '%s'\n Type: %s\n", string(key), string(value), dataType)
	return nil
}, "person", "name")

// The most efficient way to extract multiple keys is `EachKey`

paths := [][]string{
  []string{"person", "name", "fullName"},
  []string{"person", "avatars", "[0]", "url"},
  []string{"company", "url"},
}
jsonparser.EachKey(data, func(idx int, value []byte, vt jsonparser.ValueType, err error){
  switch idx {
  case 0: // []string{"person", "name", "fullName"}
    ...
  case 1: // []string{"person", "avatars", "[0]", "url"}
    ...
  case 2: // []string{"company", "url"},
    ...
  }
}, paths...)

// For more information see docs below
```

# json-iterator

json-iterator 是滴滴出品的，号称全世界最快的 JSON 解析器。它最多能比普通的解析器快 10 倍之多，即使在数据绑定的用法下也有同样的性能优势。非常易于使用的 api，允许你使用任何风格或者混搭的方式来解析 JSON 。给你前所未有的灵活性。

![JSON 解析性能比较](https://s2.ax1x.com/2019/12/02/QnMBd0.png)

## Marshal

```go
package main

import (
    "encoding/json"
    "fmt"
    "os"

    "github.com/json-iterator/go"
)

func main() {
    type ColorGroup struct {
        ID     int
        Name   string
        Colors []string
    }
    group := ColorGroup{
        ID:     1,
        Name:   "Reds",
        Colors: []string{"Crimson", "Red", "Ruby", "Maroon"},
    }
    b, err := json.Marshal(group)
    if err != nil {
        fmt.Println("error:", err)
    }
    os.Stdout.Write(b)

    var json_iterator = jsoniter.ConfigCompatibleWithStandardLibrary
    b, err = json_iterator.Marshal(group)
    os.Stdout.Write(b)
}
```

## Unmarshal

```go
package main

import (
    "encoding/json"
    "fmt"

    "github.com/json-iterator/go"
)

func main() {
    var jsonBlob = []byte(`[
        {"Name": "Platypus", "Order": "Monotremata"},
        {"Name": "Quoll",    "Order": "Dasyuromorphia"}
    ]`)
    type Animal struct {
        Name  string
        Order string
    }
    var animals []Animal
    err := json.Unmarshal(jsonBlob, &animals)
    if err != nil {
        fmt.Println("error:", err)
    }
    fmt.Printf("%+v", animals)

    var animals2 []Animal
    var json_iterator = jsoniter.ConfigCompatibleWithStandardLibrary
    json_iterator.Unmarshal(jsonBlob, &animals2)
    fmt.Printf("%+v", animals2)
}
```

## jsoniter.Get

```go
package main

import (
    "fmt"

    "github.com/json-iterator/go"
)

func main() {

    val := []byte(`{"ID":1,"Name":"Reds","Colors":["Crimson","Red","Ruby","Maroon"]}`)
    str := jsoniter.Get(val, "Colors", 0).ToString()
    fmt.Println(str)
}
```

## NewDecoder

```go
package main

import (
    "fmt"
    "strings"

    "github.com/json-iterator/go"
)

func main() {
    json := jsoniter.ConfigCompatibleWithStandardLibrary
    reader := strings.NewReader(`{"branch":"beta","change_log":"add the rows{10}","channel":"fros","create_time":"2017-06-13 16:39:08","firmware_list":"","md5":"80dee2bf7305bcf179582088e29fd7b9","note":{"CoreServices":{"md5":"d26975c0a8c7369f70ed699f2855cc2e","package_name":"CoreServices","version_code":"76","version_name":"1.0.76"},"FrDaemon":{"md5":"6b1f0626673200bc2157422cd2103f5d","package_name":"FrDaemon","version_code":"390","version_name":"1.0.390"},"FrGallery":{"md5":"90d767f0f31bcd3c1d27281ec979ba65","package_name":"FrGallery","version_code":"349","version_name":"1.0.349"},"FrLocal":{"md5":"f15a215b2c070a80a01f07bde4f219eb","package_name":"FrLocal","version_code":"791","version_name":"1.0.791"}},"pack_region_urls":{"CN":"https://s3.cn-north-1.amazonaws.com.cn/xxx-os/ttt_xxx_android_1.5.3.344.393.zip","default":"http://192.168.8.78/ttt_xxx_android_1.5.3.344.393.zip","local":"http://192.168.8.78/ttt_xxx_android_1.5.3.344.393.zip"},"pack_version":"1.5.3.344.393","pack_version_code":393,"region":"all","release_flag":0,"revision":62,"size":38966875,"status":3}`)
    decoder := json.NewDecoder(reader)
    params := make(map[string]interface{})
    err := decoder.Decode(&params)
    if err != nil {
        fmt.Println(err)
    } else {
        fmt.Printf("%+v\n", params)
    }
}
```
