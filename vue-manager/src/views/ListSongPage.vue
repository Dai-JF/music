<template>
  <div class="table">
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-tickets"></i> 歌单歌曲信息
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">
      <div class="handle-box">
        <el-input
          v-model="select_word"
          size="medium"
          placeholder="筛选关键词"
          class="handle-input mr10"
        ></el-input>
        <br /><br />
        <el-button
          type="primary"
          size="medium"
          @click="centerDialogVisible = true"
          >添加歌曲</el-button
        >
        <el-button
          type="danger"
          size="medium"
          class="handle-del mr10"
          @click="delAll"
          >批量删除</el-button
        >
      </div>
      <el-table
        :data="tableData"
        border
        size="medium"
        style="width: 100%"
        ref="multipleTable"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50"></el-table-column>
        <el-table-column prop="name" label="歌手-歌曲"></el-table-column>
        <el-table-column label="操作" width="80">
          <template v-slot="scope">
            <el-button
              size="medium"
              type="danger"
              @click="handleDelete(scope.row.id)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!--添加歌曲-->
    <el-dialog
      title="添加歌曲"
      v-model="centerDialogVisible"
      width="600px"
      center
    >
      <el-form
        :model="registerForm"
        status-icon
        ref="registerForm"
        label-width="80px"
        class="demo-ruleForm"
      >
        <el-form-item prop="singerName" label="歌手名字" size="medium">
          <el-input
            v-model="registerForm.singerName"
            placeholder="歌手名字"
          ></el-input>
        </el-form-item>
        <el-form-item prop="songName" label="歌曲名字" size="medium">
          <el-input
            v-model="registerForm.songName"
            placeholder="歌曲名字"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button size="medium" @click="centerDialogVisible = false"
            >取 消</el-button
          >
          <el-button type="primary" size="medium" @click="getSongId"
            >确 定</el-button
          >
        </span>
      </template>
    </el-dialog>

    <!-- 删除提示框 -->
    <yin-del-dialog
      :delVisible="delVisible"
      @deleteRow="deleteRow"
      @cancelRow="delVisible = $event"
    ></yin-del-dialog>
  </div>
</template>

<script>
import { mixin } from "../mixins";
import { HttpManager } from "../api/index";
import YinDelDialog from "@/components/dialog/YinDelDialog";

export default {
  name: "ListSongPage",
  mixins: [mixin],
  components: {
    YinDelDialog,
  },
  data() {
    return {
      registerForm: {
        singerName: "",
        songName: "",
      },
      tableData: [],
      tempDate: [],
      multipleSelection: [],
      centerDialogVisible: false,
      editVisible: false,
      delVisible: false,
      select_word: "",
      idx: -1,
    };
  },
  watch: {
    select_word: function () {
      if (this.select_word === "") {
        this.tableData = this.tempDate;
      } else {
        this.tableData = [];
        for (let item of this.tempDate) {
          if (item.name.includes(this.select_word)) {
            this.tableData.push(item);
          }
        }
      }
    },
  },
  created() {
    this.getData();
  },
  methods: {
    // 获取歌单
    getData() {
      this.tableData = [];
      this.tempDate = [];
      HttpManager.getListSongOfSongId(this.$route.query.id)
        .then((res) => {
          for (let item of res) {
            this.getSong(item.songId);
          }
        })
        .catch((err) => {
          console.error(err);
        });
    },
    // 获取歌单里对应的音乐
    getSong(id) {
      HttpManager.getSongOfId(id)
        .then((res) => {
          this.tableData.push(res[0]);
          this.tempDate.push(res[0]);
        })
        .catch((err) => {
          console.error(err);
        });
    },
    // 获取要添加歌曲的ID
    getSongId() {
      var id = this.registerForm.singerName + "-" + this.registerForm.songName;
      HttpManager.getSongOfSingerName(id).then((res) => {
        this.addSong(res[0].id);
      });
    },
    // 添加歌曲
    addSong(id) {
      let params = new URLSearchParams();
      params.append("songId", id);
      params.append("songListId", this.$route.query.id);
      HttpManager.setListSong(params)
        .then((res) => {
          if (res.code === 1) {
            this.getData();
            this.$notify({
              title: "添加成功",
              type: "success",
            });
          } else {
            this.$notify({
              title: "添加失败",
              type: "error",
            });
          }
        })
        .catch((err) => {
          console.error(err);
        });
      this.centerDialogVisible = false;
    },
    // 确定删除
    deleteRow() {
      HttpManager.deleteListSong(this.idx)
        .then((res) => {
          if (res) {
            this.getData();
            this.$notify({
              title: "删除成功",
              type: "success",
            });
          } else {
            this.$notify({
              title: "删除失败",
              type: "error",
            });
          }
        })
        .catch((err) => {
          console.error(err);
        });
      this.delVisible = false;
    },
  },
};
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.handle-input {
  width: 300px;
  display: inline-block;
}
</style>
