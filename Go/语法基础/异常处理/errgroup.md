# errgroup

errgroup 为我们提供了对于多个异步任务的编排特性，包括同步、异常上报、上下文取消等。

```go
package main

import (
    "context"
    "fmt"
    "io/ioutil"
    "os"
    "golang.org/x/sync/errgroup"
)

func readFiles(ctx context.Context, files []string) ([]string, error) {
    g, ctx := errgroup.WithContext(ctx)
    results := make([]string, len(files))
    for i, file := range files {
        i, file := i, file
        g.Go(func() error {
            data, err := ioutil.ReadFile(file)
            if err == nil {
                results[i] = string(data)
            }
            return err
        })
    }
    if err := g.Wait(); err != nil {
        return nil, err
    }
    return results, nil
}

func main() {
    var files = []string{
        "file1",
        "file2",
    }
    results, err := readFiles(context.Background(), files)
    if err != nil {
        fmt.Fprintln(os.Stderr, err)
        return
    }
    for _, result := range results {
        fmt.Println(result)
    }
}
```
