<template>
  <div class="table">
    <!-- b表格 -->
    <div class="container">
      <div class="handle-box">
        <el-input v-model="select_word" size="medium" placeholder="筛选关键词" class="handle-input mr10"></el-input>
        <br><br>
        <el-button type="primary" size="medium" @click="centerDialogVisible = true">添加歌单</el-button>
        <el-button type="danger" size="medium" class="handle-del mr10" @click="delAll">批量删除</el-button>
      </div>
      <el-table :data="data" border size="medium" style="width: 100%" height="550px" ref="multipleTable" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="40"></el-table-column>
        <el-table-column label="歌单图片" width="100" align="center">
          <template v-slot="scope">
            <img :src="getUrl(scope.row.pic)" alt="" style="width: 80px;"/>
              <el-upload
                class="upload-demo"
                :action="uploadUrl(scope.row.id)"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                >
                <el-button size="small">更新图片</el-button>
              </el-upload>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" width="200" align="center"></el-table-column>
        <el-table-column label="简介">
          <template v-slot="scope">
            <p style="height: 100px; overflow: scroll">{{ scope.row.introduction }}</p>
          </template>
        </el-table-column>
        <el-table-column label="风格" width="100" align="center">
          <template v-slot="scope">
            <div>{{ scope.row.style }}</div>
          </template>
        </el-table-column>
        <el-table-column label="内容" width="80" align="center">
          <template  v-slot="scope">
            <el-button size="small" @click="getContent(data[scope.$index].id)">内容</el-button>
          </template>
        </el-table-column>
        <el-table-column label="评论" width="80" align="center">
          <template  v-slot="scope">
            <el-button size="small" @click="getComment(data[scope.$index].id)">评论</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template v-slot="scope">
            <el-button
              size="small"
              @click="handleEdit(scope.row)">编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row.id)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          @current-change="handleCurrentChange"
          background
          layout="total, prev, pager, next"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="tableData.length">
        </el-pagination>
      </div>
    </div>

    <!--添加歌单-->
    <el-dialog title="添加歌单" v-model="centerDialogVisible" width="600px" center>
      <el-form :model="registerForm" status-icon ref="registerForm" label-width="70px" class="demo-ruleForm">
          <el-form-item label="歌单名" prop="title" size="medium">
            <el-input v-model="registerForm.title" placeholder="歌单名"></el-input>
          </el-form-item>
          <el-form-item label="歌单介绍" prop="introduction" size="medium">
            <el-input v-model="registerForm.introduction" placeholder="歌单介绍"></el-input>
          </el-form-item>
          <el-form-item label="风格" prop="style" size="medium">
          <el-select v-model="registerForm.style" placeholder="风格">
            <el-option v-for="item in stylies" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button size="small" @click="centerDialogVisible = false">取 消</el-button>
          <el-button type="primary" size="small" @click="addsongList">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑弹出框 -->
    <el-dialog title="编辑" v-model="editVisible" width="600px">
      <el-form ref="form" :model="form" label-width="40px">
        <el-form-item label="标题" size="medium">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="简介" size="medium">
          <el-input  type="textarea" v-model="form.introduction"></el-input>
        </el-form-item>
        <el-form-item label="风格" prop="style" size="medium">
          <el-select v-model="form.style" placeholder="风格">
            <el-option v-for="item in stylies" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button size="small" @click="editVisible = false">取 消</el-button>
          <el-button type="primary" size="small" @click="saveEdit">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除提示框 -->
    <yin-del-dialog :delVisible="delVisible" @deleteRow="deleteRow" @cancelRow="delVisible = $event"></yin-del-dialog>
  </div>
</template>

<script>
import { mixin } from '../mixins'
import { HttpManager } from '../api/index'
import { STYLE,LIST_SONG, COMMENT } from '../enums'
import YinDelDialog from '@/components/dialog/YinDelDialog'

export default {
  name: 'SongListPage',
  mixins: [mixin],
  components: {
    YinDelDialog
  },
  data () {
    return {
      registerForm: {
        title: '',
        introduction: '',
        style: ''
      },
      form: {
        id: '',
        title: '',
        pic: '',
        introduction: '',
        style: ''
      },
      stylies:STYLE,
      tableData: [],
      tempDate: [],
      multipleSelection: [], // 记录要删除的歌单
      centerDialogVisible: false,
      editVisible: false,
      delVisible: false,
      select_word: '',
      pageSize: 5, // 页数
      currentPage: 1, // 当前页
      idx: -1
    }
  },
  computed: {
    // 计算当前表格中的数据
    data () {
      return this.tableData.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
    }
  },
  watch: {
    select_word: function () {
      if (this.select_word === '') {
        this.tableData = this.tempDate
      } else {
        this.tableData = []
        for (let item of this.tempDate) {
          if (item.title.includes(this.select_word)) {
            this.tableData.push(item)
          }
        }
        for (let item of this.tempDate) {
          if (item.introduction.includes(this.select_word)) {
            this.tableData.push(item)
          }
        }
        for (let item of this.tempDate) {
          if (item.style.includes(this.select_word)) {
            this.tableData.push(item)
          }
        }
      }
    }
  },
  created () {
    this.getData()
  },
  methods: {
    uploadUrl (id) {
      return `${this.$store.state.HOST}/songList/img/update?id=${id}`
    },
    // 获取歌单信息
    getData () {
      this.tableData = []
      this.tempDate = []
      HttpManager.getSongList().then((res) => {
        this.tableData = res
        this.tempDate = res
        this.currentPage = 1
      })
    },
    // 获取当前页
    handleCurrentChange (val) {
      this.currentPage = val
    },
    getContent (id) {
      this.routerManager(LIST_SONG, { path: LIST_SONG, query: { id } })
    },
    getComment (id) {
      this.routerManager(COMMENT, { path: COMMENT, query: { id, type: 1 } })
    },
    // 编辑啊
    handleEdit (row) {
      this.idx = row.id
      this.form = {
        id: row.id,
        title: row.title,
        pic: row.pic,
        introduction: row.introduction,
        style: row.style
      }
      this.editVisible = true
    },
    // 保存编辑
    saveEdit () {
      let params = new URLSearchParams()
      params.append('id', this.form.id)
      params.append('title', this.form.title)
      params.append('pic', this.form.pic)
      params.append('introduction', this.form.introduction)
      params.append('style', this.form.style)
      HttpManager.updateSongListMsg(params)
        .then(res => {
          if (res.code === 1) {
            this.$notify({
              title: '编辑成功',
              type: 'success'
            })
            this.getData()
          } else {
            this.$notify({
              title: '编辑失败',
              type: 'error'
            })
          }
        })
        .catch(err => {
          console.error(err)
        })
      this.editVisible = false
    },
    // 添加歌单
    addsongList () {
      let params = new URLSearchParams()
      params.append('title', this.registerForm.title)
      params.append('pic', '/img/songListPic/songList.jpg')
      params.append('introduction', this.registerForm.introduction)
      params.append('style', this.registerForm.style)
      HttpManager.setSongList(params).then(res => {
        if (res.code === 1) {
          this.getData()
          this.registerForm = {}
          this.$notify({
            title: '添加成功',
            type: 'success'
          })
        } else {
          this.$notify({
            title: '添加失败',
            type: 'error'
          })
        }
      }).catch(err => {
        console.error(err)
      })
      this.centerDialogVisible = false
    },
    // 确定删除
    deleteRow () {
      HttpManager.deleteSongList(this.idx)
        .then(res => {
          if (res) {
            this.getData()
            this.$notify({
              title: '删除成功',
              type: 'success'
            })
          } else {
            this.$notify({
              title: '删除失败',
              type: 'error'
            })
          }
        })
        .catch(err => {
          console.error(err)
        })
      this.delVisible = false
    }
  }
}
</script>

<style scoped>
.handle-box {
    margin-bottom: 20px;
}

.handle-input {
    width: 300px;
    display: inline-block;
}
.pagination {
    display: flex;
    justify-content: center;
}
</style>
