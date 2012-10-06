class CorrectionsController < ApplicationController
  respond_to :html, :except => :sync

  def index
    respond_with(@corrections = Correction.all)
  end

  # Accept and save an array of corrections, returning a corresponding
  # array with the status of each save. Each correction to be saved should
  # have a +sync_id+ attribute; this attribute is not saved, and only
  # needs to be unique for each correction in this call. The return status
  # array will have each status tagged with the matching +sync_id+.
  #
  # == JSON POST format
  #
  #   {"corrections":[{"sync_id":17,"current_text":"To Kil A Mockingbird"},{"sync_id":21,"current_text":""}]}
  #
  # == JSON return format
  #
  #   [{"sync_id":17,"status":"ok"},{"sync_id":21,"status":"error","errors":["Current text cannot be blank"]}]
  #
  def sync
    @statuses = params[:corrections].collect {|c| sync_one(c) }
    respond_to do |format|
      format.json { render :json => @statuses }
    end
  end

private

  def sync_one(correction_hash)
    sync_id = correction_hash.delete('sync_id')
    begin
      corr = Correction.new(correction_hash)
      return {:sync_id => sync_id, :status => :ok} if corr.save
      return {:sync_id => sync_id, :status => :error, :errors => corr.errors.full_messages}
    rescue ActiveModel::MassAssignmentSecurity::Error => e
      return {:sync_id => sync_id, :status => :error, :errors => [e.message]}
    end
  end
end
