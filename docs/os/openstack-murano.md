# Openstack murano打包部署应用

## 简介

Murano是一个开源OpenStack项目，它结合了application catalog和通用工具，以简化和加速打包和部署。它几乎可以与OpenStack中的任何应用程序和服务一起使用。

![Murano架构图](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/murano.png)

## 安装

依据文档安装

[murano官方说明文档](https://murano.readthedocs.io/en/stable-liberty/administrator-guide/admin_index.html)

## 部署应用

部署不可避免会使用的MuranoPL语言格式。

[官方介绍](https://murano.readthedocs.io/en/stable-liberty/appdev-guide/murano_pl.html)

[如何使用murano的PL语言](https://blog.csdn.net/canxinghen/article/details/61615823)

### Hot Package

任务需要部署的是 heat 模板，只是一个yaml文件，发现文档中有关于部署heat模板应用的专门模块就叫做Hot Package。相关文档：

[Hot Package](https://murano.readthedocs.io/en/stable-liberty/appdev-guide/hot_packages.html)

主要分为手动和cli命令自动打包两种方式，如何导入logo，heat模板，以及定义App信息，以及嵌套模板的用法上面都介绍的很详细了。但是在单纯用Heat dashboard部署Stack的时候，可以加入environment文件，填充默认值，可Murano的包如何做到这一点呢，百般搜索也找不到相关的解答，只能看源码了。

下载Murano工程，找到hot_package.py

**可以看到嵌套模板和环境的默认目录名**

```python
RESOURCES_DIR_NAME = 'Resources/'
HOT_FILES_DIR_NAME = 'HotFiles/'
HOT_ENV_DIR_NAME = 'HotEnvironments/'
```

发现Hot package实际也是基于PL语言，只不过是自动生成的

```python
    @staticmethod
    def _generate_workflow(hot, files):
        hot_files_map = {}
        for f in files:
            file_path = "$resources.string('{0}{1}')".format(
                HOT_FILES_DIR_NAME, f)
            hot_files_map[f] = YAQL(file_path)

        hot_env = YAQL("$.hotEnvironment")

        deploy = [
            {YAQL('$environment'): YAQL(
                "$.find('io.murano.Environment').require()"
            )},
            {YAQL('$reporter'): YAQL(
                "new('io.murano.system.StatusReporter', "
                "environment => $environment)")},
            {
                'If': YAQL('$.getAttr(generatedHeatStackName) = null'),
                'Then': [
                    YAQL("$.setAttr(generatedHeatStackName, "
                         "'{0}_{1}'.format(randomName(), id($environment)))")
                ]
            },
            {YAQL('$stack'): YAQL(
                "new('io.murano.system.HeatStack', $environment, "
                "name => $.getAttr(generatedHeatStackName))")},

            YAQL("$reporter.report($this, "
                 "'Application deployment has started')"),

            {YAQL('$resources'): YAQL("new('io.murano.system.Resources')")},

            {YAQL('$template'): YAQL("$resources.yaml(type($this))")},
            YAQL('$stack.setTemplate($template)'),
            {YAQL('$parameters'): YAQL("$.templateParameters")},
            YAQL('$stack.setParameters($parameters)'),
            {YAQL('$files'): hot_files_map},
            YAQL('$stack.setFiles($files)'),
            {YAQL('$hotEnv'): hot_env},
            {
                'If': YAQL("bool($hotEnv)"),
                'Then': [
                    {YAQL('$envRelPath'): YAQL("'{0}' + $hotEnv".format(
                        HOT_ENV_DIR_NAME))},
                    {YAQL('$hotEnvContent'): YAQL("$resources.string("
                                                  "$envRelPath)")},
                    YAQL('$stack.setHotEnvironment($hotEnvContent)')
                ]
            },

            YAQL("$reporter.report($this, 'Stack creation has started')"),
            {
                'Try': [YAQL('$stack.push()')],
                'Catch': [
                    {
                        'As': 'e',
                        'Do': [
                            YAQL("$reporter.report_error($this, $e.message)"),
                            {'Rethrow': None}
                        ]
                    }
                ],
                'Else': [
                    {YAQL('$.templateOutputs'): YAQL('$stack.output()')},
                    YAQL("$reporter.report($this, "
                         "'Stack was successfully created')"),

                    YAQL("$reporter.report($this, "
                         "'Application deployment has finished')"),
                ]
            }
        ]

        destroy = [
            {YAQL('$environment'): YAQL(
                "$.find('io.murano.Environment').require()"
            )},
            {YAQL('$stack'): YAQL(
                "new('io.murano.system.HeatStack', $environment, "
                "name => $.getAttr(generatedHeatStackName))")},
            YAQL('$stack.delete()')
        ]

        return {
            'Methods': {
                'deploy': {
                    'Body': deploy
                },
                'destroy': {
                    'Body': destroy
                }
            }
        }
```

**找到其中设置参数的部分是读 templateParameters**

```python
def _translate_class(self):
    # 省略
    	hot_envs_path = path.secure_join(
        self._source_directory, RESOURCES_DIR_NAME, HOT_ENV_DIR_NAME)

        # if using hot environments, doing parameter validation with contracts
        # will overwrite the parameters in the hot environment.
        # don't validate parameters if hot environments exist.
        validate_hot_parameters = (not os.path.isdir(hot_envs_path) or
                                   not os.listdir(hot_envs_path))

        parameters = HotPackage._build_properties(hot, validate_hot_parameters)
        parameters.update(HotPackage._translate_outputs(hot))
        translated['Properties'] = parameters
   # 省略

def _build_properties(hot, validate_hot_parameters):
    result = {
        'generatedHeatStackName': {
            'Contract': YAQL('$.string()'),
            'Usage': 'Out'
        },
        'hotEnvironment': {
            'Contract': YAQL('$.string()'),
            'Usage': 'In'
        },
        'name': {
            'Contract': YAQL('$.string().notNull()'),
            'Usage': 'In',

        }
    }

    if validate_hot_parameters:
        params_dict = {}
        for key, value in (hot.get('parameters') or {}).items():
            param_contract = HotPackage._translate_param_to_contract(value)
            params_dict[key] = param_contract
            result['templateParameters'] = {
                'Contract': params_dict,
                'Default': {},
                'Usage': 'In'
            }
            else:
                result['templateParameters'] = {
                    'Contract': {},
                    'Default': {},
                    'Usage': 'In'
                }

                return result
```

总结，其实murano的只是根据提供的环境文件中的参数去校验，不会为我们填写default值，想了想如何把大部分参数都写死在包里的env文件中，更改是相当不方便的。放弃！