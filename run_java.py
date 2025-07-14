import subprocess

def run_java_program(data_num,es_num,EXPERIMENT_TIME,filepath,replicapath):
    # 启动 Java 程序
    process = subprocess.Popen(
        ['java', '-cp', 'target/classes', 'com.fchen_group.EDISVMD.Run.Benchmark'],  # 确保你的 Java 类名正确
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )

    # 提供多个输入
    inputs = f"{data_num}\n{es_num}\n{EXPERIMENT_TIME}\n{filepath}\n{replicapath}\n"  # 输入名字和年龄
    stdout, stderr = process.communicate(input=inputs)

    # 打印输出
    print("程序输出:")
    print(stdout)

    # 打印错误（如果有）
    if stderr:
        print("程序错误:")
        print(stderr)

data_num = ["16", "32", "64", "128", "256", "512", "1024", "128", "128", "128", "128", "128", "128", "128", "128", "128", "128", "128", "128","128"]

es_num = ["128", "128", "128", "128", "128", "128", "128", "16", "32", "64", "128", "256", "512", "1024", "128", "128", "128", "128", "128", "128"]

EXPERIMENT_TIME="50"

filepath=["D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\16MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\32MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\128MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\256MB.txt",
          "D:\\EDI-QZF\\experiment\\AppVendor\\512MB.txt"]

replicapath=["D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\16MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\32MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\64MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\128MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\256MB.txt",
             "D:\\EDI-QZF\\experiment\\EdgeServer\\512MB.txt"]

if __name__ == "__main__":
    # 多次运行
    number_of_runs = 20  # 设置你想运行的次数
    for i in range(number_of_runs):
        dn = data_num[i]
        esn=es_num[i]
        fp=filepath[i]
        rp=replicapath[i]

#         dn = "1"
#         esn = "1"
#         fp = "D:\\EDI-QZF\\experiment\\AppVendor\\16MB.txt"
#         rp = "D:\\EDI-QZF\\experiment\\EdgeServer\\16MB.txt"
#         EXPERIMENT_TIME = "1"

        run_java_program(dn,esn,EXPERIMENT_TIME,fp,rp)
#         run_java_program(SECTOR_NUMBER, TAG_SIZE,1,1,fp,rp)